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
import com.ruoyi.site.domain.SiteArticle;
import com.ruoyi.site.domain.SiteArticleDraft;
import com.ruoyi.site.domain.SiteArticleRevision;
import com.ruoyi.site.domain.SitePublicationState;
import com.ruoyi.site.mapper.SiteArticleAdminMapper;
import com.ruoyi.site.service.ISiteArticleAdminService;

@Service
public class SiteArticleAdminServiceImpl implements ISiteArticleAdminService
{
    private static final Pattern DANGEROUS_TAG = Pattern.compile("(?is)<\\s*(script|style|iframe|object|embed|link|base)\\b");
    private static final Pattern EVENT_HANDLER = Pattern.compile("(?is)\\son[a-z0-9_-]+\\s*=");
    private static final Pattern JAVASCRIPT_URL = Pattern.compile("(?is)javascript\\s*:");

    @Autowired
    private SiteArticleAdminMapper articleMapper;

    @Autowired
    private SiteMediaReferenceService mediaReferenceService;

    @Override
    public List<SiteArticle> selectSiteArticleList(SiteArticle article)
    {
        return articleMapper.selectSiteArticleList(article);
    }

    @Override
    public SiteArticleDraft selectArticleDraftById(Long articleId)
    {
        SiteArticle article = articleMapper.selectSiteArticleById(articleId);
        if (article == null)
        {
            return null;
        }
        SiteArticleRevision revision = articleMapper.selectCurrentDraftByArticleId(articleId);
        if (revision == null && article.getPublishedRevisionId() != null)
        {
            revision = articleMapper.selectRevisionById(article.getPublishedRevisionId());
        }
        return toDraft(article, revision);
    }

    @Override
    @Transactional
    public SiteArticleDraft saveDraft(SiteArticleDraft draft)
    {
        validateDraft(draft);
        Long categoryId = articleMapper.selectCategoryIdByCode(trim(draft.getCategoryCode()));
        if (categoryId == null)
        {
            throw new ServiceException("新闻分类不存在或已停用");
        }
        if (draft.getArticleId() == null)
        {
            SiteArticle article = toArticle(draft, categoryId);
            article.setCreateBy(draft.getCreateBy());
            articleMapper.insertSiteArticle(article);
            SiteArticleRevision revision = toRevision(draft, article.getArticleId(), 1);
            articleMapper.insertArticleRevision(revision);
            synchronizeArticleMedia(revision);
            article.setDraftRevisionId(revision.getRevisionId());
            article.setUpdateBy(draft.getCreateBy());
            articleMapper.updateRevisionPointers(article);
            return toDraft(article, revision);
        }

        SiteArticle article = articleMapper.selectSiteArticleForUpdate(draft.getArticleId());
        if (article == null)
        {
            throw new ServiceException("新闻不存在或已删除");
        }
        applyArticleInput(article, draft, categoryId);
        article.setUpdateBy(draft.getUpdateBy());
        articleMapper.updateSiteArticle(article);

        SiteArticleRevision revision = articleMapper.selectCurrentDraftByArticleId(article.getArticleId());
        if (revision == null)
        {
            revision = toRevision(draft, article.getArticleId(), articleMapper.selectNextRevisionNo(article.getArticleId()));
            articleMapper.insertArticleRevision(revision);
            synchronizeArticleMedia(revision);
            article.setDraftRevisionId(revision.getRevisionId());
            articleMapper.updateRevisionPointers(article);
        }
        else
        {
            applyRevisionInput(revision, draft);
            revision.setUpdateBy(draft.getUpdateBy());
            articleMapper.updateDraftArticleRevision(revision);
            synchronizeArticleMedia(revision);
        }
        return toDraft(article, revision);
    }

    @Override
    @Transactional
    public void publishArticle(Long articleId, String operator, String reason)
    {
        SiteArticle article = articleMapper.selectSiteArticleForUpdate(articleId);
        SiteArticleRevision draft = article == null ? null : articleMapper.selectCurrentDraftByArticleId(articleId);
        if (article == null || draft == null || !articleId.equals(draft.getArticleId())
                || !SitePublicationState.DRAFT.name().equals(draft.getRevisionState()))
        {
            throw new ServiceException("新闻没有可发布的草稿");
        }
        Long previous = article.getPublishedRevisionId();
        if (previous != null)
        {
            articleMapper.updateRevisionState(previous, SitePublicationState.SUPERSEDED.name(), operator);
        }
        articleMapper.updateRevisionState(draft.getRevisionId(), SitePublicationState.PUBLISHED.name(), operator);
        article.setPreviousPublishedRevisionId(previous);
        article.setPublishedRevisionId(draft.getRevisionId());
        article.setDraftRevisionId(null);
        article.setUpdateBy(operator);
        articleMapper.updateRevisionPointers(article);
        articleMapper.insertPublishAudit(articleId, "PUBLISH", previous, draft.getRevisionId(), operator, trim(reason));
    }

    @Override
    @Transactional
    public void unpublishArticle(Long articleId, String operator, String reason)
    {
        SiteArticle article = articleMapper.selectSiteArticleForUpdate(articleId);
        if (article == null || article.getPublishedRevisionId() == null)
        {
            throw new ServiceException("新闻当前没有可下架的发布修订");
        }
        Long published = article.getPublishedRevisionId();
        articleMapper.updateRevisionState(published, SitePublicationState.ARCHIVED.name(), operator);
        article.setPreviousPublishedRevisionId(published);
        article.setPublishedRevisionId(null);
        article.setUpdateBy(operator);
        articleMapper.updateRevisionPointers(article);
        articleMapper.insertPublishAudit(articleId, "UNPUBLISH", published, null, operator, trim(reason));
    }

    private SiteArticle toArticle(SiteArticleDraft draft, Long categoryId)
    {
        SiteArticle article = new SiteArticle();
        applyArticleInput(article, draft, categoryId);
        article.setRemark(draft.getRemark());
        return article;
    }

    private void applyArticleInput(SiteArticle article, SiteArticleDraft draft, Long categoryId)
    {
        article.setArticleCode(trim(draft.getArticleCode()).toUpperCase(Locale.ROOT));
        article.setLegacyPath(normalizeRoute(draft.getLegacyPath()));
        article.setCategoryId(categoryId);
        article.setRemark(draft.getRemark());
    }

    private SiteArticleRevision toRevision(SiteArticleDraft draft, Long articleId, int revisionNo)
    {
        SiteArticleRevision revision = new SiteArticleRevision();
        revision.setArticleId(articleId);
        revision.setRevisionNo(revisionNo);
        revision.setRevisionState(SitePublicationState.DRAFT.name());
        revision.setCreateBy(draft.getCreateBy());
        applyRevisionInput(revision, draft);
        return revision;
    }

    private void applyRevisionInput(SiteArticleRevision revision, SiteArticleDraft draft)
    {
        revision.setTitle(trim(draft.getTitle()));
        revision.setSummary(trim(draft.getSummary()));
        revision.setBodyHtml(sanitizeHtml(draft.getBodyHtml()));
        revision.setSeoJson(trim(draft.getSeoJson()));
        revision.setPublishedAt(draft.getPublishedAt());
        revision.setContentHash(sha256(revision.getTitle() + "\n" + revision.getSummary() + "\n" + revision.getBodyHtml()
                + "\n" + revision.getSeoJson() + "\n" + revision.getPublishedAt()));
        revision.setRemark(draft.getRemark());
    }

    private SiteArticleDraft toDraft(SiteArticle article, SiteArticleRevision revision)
    {
        SiteArticleDraft result = new SiteArticleDraft();
        result.setArticleId(article.getArticleId());
        result.setArticleCode(article.getArticleCode());
        result.setLegacyPath(article.getLegacyPath());
        result.setCategoryCode(article.getCategoryCode());
        result.setDraftRevisionId(article.getDraftRevisionId());
        result.setPublishedRevisionId(article.getPublishedRevisionId());
        if (revision != null)
        {
            result.setRevisionId(revision.getRevisionId());
            result.setRevisionNo(revision.getRevisionNo());
            result.setRevisionState(revision.getRevisionState());
            result.setTitle(revision.getTitle());
            result.setSummary(revision.getSummary());
            result.setBodyHtml(revision.getBodyHtml());
            result.setSeoJson(revision.getSeoJson());
            result.setContentHash(revision.getContentHash());
            result.setPublishedAt(revision.getPublishedAt());
            result.setPublishedBy(revision.getPublishedBy());
            result.setRemark(revision.getRemark());
        }
        return result;
    }

    private void synchronizeArticleMedia(SiteArticleRevision revision)
    {
        mediaReferenceService.synchronizeHtmlReferences("ARTICLE_REVISION", revision.getRevisionId(), revision.getBodyHtml());
    }

    private void validateDraft(SiteArticleDraft draft)
    {
        require(draft.getArticleCode(), "新闻编码不能为空");
        require(draft.getLegacyPath(), "公开路径不能为空");
        require(draft.getCategoryCode(), "新闻分类不能为空");
        require(draft.getTitle(), "新闻标题不能为空");
        if (!trim(draft.getArticleCode()).matches("[A-Za-z][A-Za-z0-9_]{0,99}"))
        {
            throw new ServiceException("新闻编码只支持字母、数字和下划线，且须以字母开头");
        }
    }

    private String normalizeRoute(String routePath)
    {
        String route = trim(routePath);
        if (StringUtils.isEmpty(route) || route.contains("..") || route.contains("\\\\") || route.contains("?") || !route.endsWith(".html"))
        {
            throw new ServiceException("新闻公开路径必须是安全的 .html 路径");
        }
        return route.startsWith("/") ? route : "/" + route;
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
            throw new ServiceException("新闻正文不能包含脚本、样式、嵌入对象、事件处理器或 javascript 链接");
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
