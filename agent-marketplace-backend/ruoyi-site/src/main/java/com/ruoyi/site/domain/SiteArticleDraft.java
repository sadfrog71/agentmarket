package com.ruoyi.site.domain;

/** 后台新闻编辑命令对象，保留公开路径和分类等根对象字段。 */
public class SiteArticleDraft extends SiteArticleRevision
{
    private static final long serialVersionUID = 1L;

    private Long articleId;
    private String articleCode;
    private String legacyPath;
    private String categoryCode;
    private Long draftRevisionId;
    private Long publishedRevisionId;

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public String getLegacyPath() { return legacyPath; }
    public void setLegacyPath(String legacyPath) { this.legacyPath = legacyPath; }
    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
    public Long getDraftRevisionId() { return draftRevisionId; }
    public void setDraftRevisionId(Long draftRevisionId) { this.draftRevisionId = draftRevisionId; }
    public Long getPublishedRevisionId() { return publishedRevisionId; }
    public void setPublishedRevisionId(Long publishedRevisionId) { this.publishedRevisionId = publishedRevisionId; }
}
