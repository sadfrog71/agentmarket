package com.ruoyi.site.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.site.domain.SitePage;
import com.ruoyi.site.domain.SitePageDraft;
import com.ruoyi.site.domain.SitePageRevision;
import com.ruoyi.site.domain.SitePublicationState;
import com.ruoyi.site.domain.SitePublicPage;
import com.ruoyi.site.mapper.SitePageMapper;
import com.ruoyi.site.service.ISitePageService;
import com.ruoyi.site.service.ISitePublicationPolicyService;

@Service
public class SitePageServiceImpl implements ISitePageService
{
    private static final Pattern DANGEROUS_TAG = Pattern.compile("(?is)<\\s*(script|style|iframe|object|embed|link|base)\\b");
    private static final Pattern EVENT_HANDLER = Pattern.compile("(?is)\\son[a-z0-9_-]+\\s*=");
    private static final Pattern JAVASCRIPT_URL = Pattern.compile("(?is)javascript\\s*:");

    @Autowired
    private SitePageMapper pageMapper;

    @Autowired
    private ISitePublicationPolicyService publicationPolicyService;

    @Autowired
    private SiteMediaReferenceService mediaReferenceService;

    @Override
    public List<SitePage> selectSitePageList(SitePage page)
    {
        return pageMapper.selectSitePageList(page);
    }

    @Override
    public SitePageDraft selectPageDraftById(Long pageId)
    {
        SitePage page = pageMapper.selectSitePageById(pageId);
        if (page == null)
        {
            return null;
        }
        SitePageRevision revision = pageMapper.selectCurrentDraftByPageId(pageId);
        if (revision == null && page.getPublishedRevisionId() != null)
        {
            revision = pageMapper.selectRevisionById(page.getPublishedRevisionId());
        }
        return toDraft(page, revision);
    }

    @Override
    @Transactional
    public SitePageDraft saveDraft(SitePageDraft draft)
    {
        validateDraft(draft);
        if (draft.getPageId() == null)
        {
            SitePage page = toPage(draft);
            page.setCreateBy(draft.getCreateBy());
            pageMapper.insertSitePage(page);
            SitePageRevision revision = toRevision(draft, page.getPageId(), 1);
            pageMapper.insertPageRevision(revision);
            synchronizePageMedia(revision);
            page.setDraftRevisionId(revision.getRevisionId());
            page.setUpdateBy(draft.getCreateBy());
            pageMapper.updateRevisionPointers(page);
            return toDraft(page, revision);
        }

        SitePage page = pageMapper.selectSitePageForUpdate(draft.getPageId());
        if (page == null)
        {
            throw new ServiceException("官网页面不存在或已删除");
        }
        applyPageInput(page, draft);
        page.setUpdateBy(draft.getUpdateBy());
        pageMapper.updateSitePage(page);

        SitePageRevision revision = pageMapper.selectCurrentDraftByPageId(page.getPageId());
        if (revision == null)
        {
            revision = toRevision(draft, page.getPageId(), pageMapper.selectNextRevisionNo(page.getPageId()));
            pageMapper.insertPageRevision(revision);
            synchronizePageMedia(revision);
            page.setDraftRevisionId(revision.getRevisionId());
            pageMapper.updateRevisionPointers(page);
        }
        else
        {
            applyRevisionInput(revision, draft);
            revision.setUpdateBy(draft.getUpdateBy());
            pageMapper.updateDraftPageRevision(revision);
            synchronizePageMedia(revision);
        }
        return toDraft(page, revision);
    }

    @Override
    @Transactional
    public void publishPage(Long pageId, String operator, String reason)
    {
        SitePage page = pageMapper.selectSitePageForUpdate(pageId);
        if (page == null)
        {
            throw new ServiceException("官网页面不存在或已删除");
        }
        SitePageRevision draft = pageMapper.selectCurrentDraftByPageId(pageId);
        publicationPolicyService.validatePagePublish(page, draft);
        Long previousPublishedRevisionId = page.getPublishedRevisionId();
        if (previousPublishedRevisionId != null)
        {
            pageMapper.updateRevisionState(previousPublishedRevisionId, SitePublicationState.SUPERSEDED.name(), operator);
        }
        pageMapper.updateRevisionState(draft.getRevisionId(), SitePublicationState.PUBLISHED.name(), operator);
        page.setPreviousPublishedRevisionId(previousPublishedRevisionId);
        page.setPublishedRevisionId(draft.getRevisionId());
        page.setDraftRevisionId(null);
        page.setUpdateBy(operator);
        pageMapper.updateRevisionPointers(page);
        pageMapper.insertPublishAudit(pageId, "PUBLISH", previousPublishedRevisionId, draft.getRevisionId(), operator, trim(reason));
    }

    @Override
    @Transactional
    public void unpublishPage(Long pageId, String operator, String reason)
    {
        SitePage page = pageMapper.selectSitePageForUpdate(pageId);
        if (page == null || page.getPublishedRevisionId() == null)
        {
            throw new ServiceException("官网页面当前没有可下架的发布修订");
        }
        Long publishedRevisionId = page.getPublishedRevisionId();
        pageMapper.updateRevisionState(publishedRevisionId, SitePublicationState.ARCHIVED.name(), operator);
        page.setPreviousPublishedRevisionId(publishedRevisionId);
        page.setPublishedRevisionId(null);
        page.setUpdateBy(operator);
        pageMapper.updateRevisionPointers(page);
        pageMapper.insertPublishAudit(pageId, "UNPUBLISH", publishedRevisionId, null, operator, trim(reason));
    }

    @Override
    public SitePublicPage selectPublishedPageByRoute(String routePath)
    {
        return pageMapper.selectPublishedPageByRoute(normalizeRoute(routePath));
    }

    private SitePage toPage(SitePageDraft draft)
    {
        SitePage page = new SitePage();
        applyPageInput(page, draft);
        page.setRemark(draft.getRemark());
        return page;
    }

    private void applyPageInput(SitePage page, SitePageDraft draft)
    {
        page.setPageCode(trim(draft.getPageCode()).toUpperCase(Locale.ROOT));
        page.setRoutePath(normalizeRoute(draft.getRoutePath()));
        page.setTemplateCode(trim(draft.getTemplateCode()));
        page.setRemark(draft.getRemark());
    }

    private SitePageRevision toRevision(SitePageDraft draft, Long pageId, int revisionNo)
    {
        SitePageRevision revision = new SitePageRevision();
        revision.setPageId(pageId);
        revision.setRevisionNo(revisionNo);
        revision.setRevisionState(SitePublicationState.DRAFT.name());
        revision.setCreateBy(draft.getCreateBy());
        applyRevisionInput(revision, draft);
        return revision;
    }

    private void applyRevisionInput(SitePageRevision revision, SitePageDraft draft)
    {
        revision.setTitle(trim(draft.getTitle()));
        revision.setSubtitle(trim(draft.getSubtitle()));
        revision.setBodyHtml(sanitizeHtml(draft.getBodyHtml()));
        revision.setSeoJson(trim(draft.getSeoJson()));
        revision.setPageDataJson(trim(draft.getPageDataJson()));
        revision.setContentHash(sha256(revision.getTitle() + "\n" + revision.getSubtitle() + "\n" + revision.getBodyHtml()
                + "\n" + revision.getSeoJson() + "\n" + revision.getPageDataJson()));
        revision.setRemark(draft.getRemark());
    }

    private SitePageDraft toDraft(SitePage page, SitePageRevision revision)
    {
        SitePageDraft result = new SitePageDraft();
        result.setPageId(page.getPageId());
        result.setPageCode(page.getPageCode());
        result.setRoutePath(page.getRoutePath());
        result.setTemplateCode(page.getTemplateCode());
        result.setPublishedRevisionId(page.getPublishedRevisionId());
        result.setDraftRevisionId(page.getDraftRevisionId());
        if (revision != null)
        {
            result.setRevisionId(revision.getRevisionId());
            result.setRevisionNo(revision.getRevisionNo());
            result.setRevisionState(revision.getRevisionState());
            result.setTitle(revision.getTitle());
            result.setSubtitle(revision.getSubtitle());
            result.setBodyHtml(revision.getBodyHtml());
            result.setSeoJson(revision.getSeoJson());
            result.setPageDataJson(revision.getPageDataJson());
            result.setContentHash(revision.getContentHash());
            result.setPublishedAt(revision.getPublishedAt());
            result.setPublishedBy(revision.getPublishedBy());
            result.setRemark(revision.getRemark());
        }
        return result;
    }

    private void synchronizePageMedia(SitePageRevision revision)
    {
        mediaReferenceService.synchronizeHtmlReferences("PAGE_REVISION", revision.getRevisionId(),
                revision.getBodyHtml() + "\n" + revision.getPageDataJson());
    }

    private void validateDraft(SitePageDraft draft)
    {
        require(draft.getPageCode(), "页面编码不能为空");
        require(draft.getRoutePath(), "公开路径不能为空");
        require(draft.getTemplateCode(), "固定模板不能为空");
        require(draft.getTitle(), "页面标题不能为空");
        if (!trim(draft.getPageCode()).matches("[A-Za-z][A-Za-z0-9_]{0,99}"))
        {
            throw new ServiceException("页面编码只支持字母、数字和下划线，且须以字母开头");
        }
    }

    private String normalizeRoute(String routePath)
    {
        String route = trim(routePath);
        if (StringUtils.isEmpty(route) || route.contains("..") || route.contains("\\\\") || route.contains("?"))
        {
            throw new ServiceException("公开路径格式不正确");
        }
        if (!route.startsWith("/"))
        {
            route = "/" + route;
        }
        return "/".equals(route) ? "/index.html" : route;
    }

    private String sanitizeHtml(String value)
    {
        String html = trim(value);
        if (StringUtils.isEmpty(html))
        {
            return "";
        }
        if (DANGEROUS_TAG.matcher(html).find() || EVENT_HANDLER.matcher(html).find() || JAVASCRIPT_URL.matcher(html).find())
        {
            throw new ServiceException("页面正文不能包含脚本、样式、嵌入对象、事件处理器或 javascript 链接");
        }
        return html;
    }

    private String sha256(String value)
    {
        try
        {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder output = new StringBuilder(hash.length * 2);
            for (byte item : hash)
            {
                output.append(String.format("%02x", item));
            }
            return output.toString();
        }
        catch (NoSuchAlgorithmException error)
        {
            throw new IllegalStateException("当前JVM不支持SHA-256", error);
        }
    }

    private void require(String value, String message)
    {
        if (StringUtils.isEmpty(trim(value)))
        {
            throw new ServiceException(message);
        }
    }

    private String trim(String value)
    {
        return value == null ? null : value.trim();
    }
}
