package com.ruoyi.site.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.site.domain.SiteCredential;
import com.ruoyi.site.domain.SiteCredentialDraft;
import com.ruoyi.site.domain.SiteCredentialRevision;
import com.ruoyi.site.domain.SitePublicationState;
import com.ruoyi.site.mapper.SiteCredentialAdminMapper;
import com.ruoyi.site.mapper.SiteMediaMapper;
import com.ruoyi.site.service.ISiteCredentialAdminService;

@Service
public class SiteCredentialAdminServiceImpl implements ISiteCredentialAdminService
{
    @Autowired
    private SiteCredentialAdminMapper credentialMapper;

    @Autowired
    private SiteMediaMapper mediaMapper;

    @Autowired
    private SiteMediaReferenceService mediaReferenceService;

    @Override
    public List<SiteCredential> selectSiteCredentialList(SiteCredential credential)
    {
        return credentialMapper.selectSiteCredentialList(credential);
    }

    @Override
    public SiteCredentialDraft selectCredentialDraftById(Long credentialId)
    {
        SiteCredential credential = credentialMapper.selectSiteCredentialById(credentialId);
        if (credential == null) return null;
        SiteCredentialRevision revision = credentialMapper.selectCurrentDraftByCredentialId(credentialId);
        if (revision == null && credential.getPublishedRevisionId() != null)
        {
            revision = credentialMapper.selectRevisionById(credential.getPublishedRevisionId());
        }
        return toDraft(credential, revision);
    }

    @Override
    @Transactional
    public SiteCredentialDraft saveDraft(SiteCredentialDraft draft)
    {
        validateDraft(draft);
        Long mediaId = mediaMapper.selectAvailableMediaIdByPublicId(trim(draft.getDocumentPublicId()));
        if (mediaId == null) throw new ServiceException("证书文件媒体不存在、尚未入库或未就绪");
        if (draft.getCredentialId() == null)
        {
            SiteCredential credential = toCredential(draft);
            credential.setCreateBy(draft.getCreateBy());
            credentialMapper.insertSiteCredential(credential);
            SiteCredentialRevision revision = toRevision(draft, credential.getCredentialId(), 1, mediaId);
            credentialMapper.insertCredentialRevision(revision);
            mediaReferenceService.synchronizeDocumentReference(revision.getRevisionId(), mediaId);
            credential.setDraftRevisionId(revision.getRevisionId());
            credential.setUpdateBy(draft.getCreateBy());
            credentialMapper.updateRevisionPointers(credential);
            return toDraft(credential, revision);
        }
        SiteCredential credential = credentialMapper.selectSiteCredentialForUpdate(draft.getCredentialId());
        if (credential == null) throw new ServiceException("资质证书不存在或已删除");
        applyCredentialInput(credential, draft);
        credential.setUpdateBy(draft.getUpdateBy());
        credentialMapper.updateSiteCredential(credential);
        SiteCredentialRevision revision = credentialMapper.selectCurrentDraftByCredentialId(credential.getCredentialId());
        if (revision == null)
        {
            revision = toRevision(draft, credential.getCredentialId(), credentialMapper.selectNextRevisionNo(credential.getCredentialId()), mediaId);
            credentialMapper.insertCredentialRevision(revision);
            mediaReferenceService.synchronizeDocumentReference(revision.getRevisionId(), mediaId);
            credential.setDraftRevisionId(revision.getRevisionId());
            credentialMapper.updateRevisionPointers(credential);
        }
        else
        {
            applyRevisionInput(revision, draft, mediaId);
            revision.setUpdateBy(draft.getUpdateBy());
            credentialMapper.updateDraftCredentialRevision(revision);
            mediaReferenceService.synchronizeDocumentReference(revision.getRevisionId(), mediaId);
        }
        return toDraft(credential, revision);
    }

    @Override
    @Transactional
    public void publishCredential(Long credentialId, String operator, String reason)
    {
        SiteCredential credential = credentialMapper.selectSiteCredentialForUpdate(credentialId);
        SiteCredentialRevision draft = credential == null ? null : credentialMapper.selectCurrentDraftByCredentialId(credentialId);
        if (credential == null || draft == null || !credentialId.equals(draft.getCredentialId()) || !SitePublicationState.DRAFT.name().equals(draft.getRevisionState()))
            throw new ServiceException("资质证书没有可发布的草稿");
        Long previous = credential.getPublishedRevisionId();
        if (previous != null) credentialMapper.updateRevisionState(previous, SitePublicationState.SUPERSEDED.name(), operator);
        credentialMapper.updateRevisionState(draft.getRevisionId(), SitePublicationState.PUBLISHED.name(), operator);
        credential.setPreviousPublishedRevisionId(previous);
        credential.setPublishedRevisionId(draft.getRevisionId());
        credential.setDraftRevisionId(null);
        credential.setUpdateBy(operator);
        credentialMapper.updateRevisionPointers(credential);
        credentialMapper.insertPublishAudit(credentialId, "PUBLISH", previous, draft.getRevisionId(), operator, trim(reason));
    }

    @Override
    @Transactional
    public void unpublishCredential(Long credentialId, String operator, String reason)
    {
        SiteCredential credential = credentialMapper.selectSiteCredentialForUpdate(credentialId);
        if (credential == null || credential.getPublishedRevisionId() == null) throw new ServiceException("资质证书当前没有可下架的发布修订");
        Long published = credential.getPublishedRevisionId();
        credentialMapper.updateRevisionState(published, SitePublicationState.ARCHIVED.name(), operator);
        credential.setPreviousPublishedRevisionId(published);
        credential.setPublishedRevisionId(null);
        credential.setUpdateBy(operator);
        credentialMapper.updateRevisionPointers(credential);
        credentialMapper.insertPublishAudit(credentialId, "UNPUBLISH", published, null, operator, trim(reason));
    }

    private SiteCredential toCredential(SiteCredentialDraft draft)
    {
        SiteCredential credential = new SiteCredential();
        applyCredentialInput(credential, draft);
        credential.setRemark(draft.getRemark());
        return credential;
    }

    private void applyCredentialInput(SiteCredential credential, SiteCredentialDraft draft)
    {
        credential.setCredentialCode(trim(draft.getCredentialCode()).toUpperCase(Locale.ROOT));
        credential.setCredentialType(trim(draft.getCredentialType()).toUpperCase(Locale.ROOT));
        credential.setRemark(draft.getRemark());
    }

    private SiteCredentialRevision toRevision(SiteCredentialDraft draft, Long credentialId, int revisionNo, Long mediaId)
    {
        SiteCredentialRevision revision = new SiteCredentialRevision();
        revision.setCredentialId(credentialId);
        revision.setRevisionNo(revisionNo);
        revision.setRevisionState(SitePublicationState.DRAFT.name());
        revision.setCreateBy(draft.getCreateBy());
        applyRevisionInput(revision, draft, mediaId);
        return revision;
    }

    private void applyRevisionInput(SiteCredentialRevision revision, SiteCredentialDraft draft, Long mediaId)
    {
        revision.setTitle(trim(draft.getTitle()));
        revision.setDocumentMediaId(mediaId);
        revision.setDocumentPublicId(trim(draft.getDocumentPublicId()));
        revision.setContentHash(sha256(revision.getTitle() + "\n" + revision.getDocumentPublicId()));
        revision.setRemark(draft.getRemark());
    }

    private SiteCredentialDraft toDraft(SiteCredential credential, SiteCredentialRevision revision)
    {
        SiteCredentialDraft result = new SiteCredentialDraft();
        result.setCredentialId(credential.getCredentialId());
        result.setCredentialCode(credential.getCredentialCode());
        result.setCredentialType(credential.getCredentialType());
        result.setDraftRevisionId(credential.getDraftRevisionId());
        result.setPublishedRevisionId(credential.getPublishedRevisionId());
        if (revision != null)
        {
            result.setRevisionId(revision.getRevisionId());
            result.setRevisionNo(revision.getRevisionNo());
            result.setRevisionState(revision.getRevisionState());
            result.setTitle(revision.getTitle());
            result.setDocumentMediaId(revision.getDocumentMediaId());
            result.setDocumentPublicId(revision.getDocumentPublicId());
            result.setContentHash(revision.getContentHash());
            result.setPublishedBy(revision.getPublishedBy());
            result.setRemark(revision.getRemark());
        }
        return result;
    }

    private void validateDraft(SiteCredentialDraft draft)
    {
        require(draft.getCredentialCode(), "证书编码不能为空");
        require(draft.getCredentialType(), "证书类型不能为空");
        require(draft.getTitle(), "证书名称不能为空");
        require(draft.getDocumentPublicId(), "证书文件媒体编号不能为空");
        if (!trim(draft.getCredentialCode()).matches("[A-Za-z][A-Za-z0-9_]{0,99}")) throw new ServiceException("证书编码只支持字母、数字和下划线，且须以字母开头");
        String type = trim(draft.getCredentialType()).toUpperCase(Locale.ROOT);
        if (!"SOFTWARE_COPYRIGHT".equals(type) && !"PATENT".equals(type) && !"OTHER".equals(type)) throw new ServiceException("证书类型不受支持");
        if (!trim(draft.getDocumentPublicId()).matches("[0-9a-fA-F-]{36}")) throw new ServiceException("证书文件媒体编号格式不正确");
    }

    private String sha256(String value)
    {
        try
        {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder output = new StringBuilder(hash.length * 2);
            for (byte item : hash) output.append(String.format("%02x", item));
            return output.toString();
        }
        catch (NoSuchAlgorithmException error) { throw new IllegalStateException("当前JVM不支持SHA-256", error); }
    }

    private void require(String value, String message)
    {
        if (StringUtils.isEmpty(trim(value))) throw new ServiceException(message);
    }

    private String trim(String value) { return value == null ? null : value.trim(); }
}
