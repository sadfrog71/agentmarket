package com.ruoyi.site.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/** 企业官网新闻的稳定根对象，公开内容通过发布修订指针读取。 */
public class SiteArticle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long articleId;
    private String articleCode;
    private String legacyPath;
    private Long categoryId;
    private String categoryCode;
    private String categoryName;
    /** 当前草稿优先、否则当前已发布修订的中文标题，仅供后台列表展示。 */
    private String title;
    /** 当前草稿优先、否则当前已发布修订的置顶标记。 */
    private String topFlag;
    private Long draftRevisionId;
    private Long publishedRevisionId;
    private Long previousPublishedRevisionId;

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public String getLegacyPath() { return legacyPath; }
    public void setLegacyPath(String legacyPath) { this.legacyPath = legacyPath; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTopFlag() { return topFlag; }
    public void setTopFlag(String topFlag) { this.topFlag = topFlag; }
    public Long getDraftRevisionId() { return draftRevisionId; }
    public void setDraftRevisionId(Long draftRevisionId) { this.draftRevisionId = draftRevisionId; }
    public Long getPublishedRevisionId() { return publishedRevisionId; }
    public void setPublishedRevisionId(Long publishedRevisionId) { this.publishedRevisionId = publishedRevisionId; }
    public Long getPreviousPublishedRevisionId() { return previousPublishedRevisionId; }
    public void setPreviousPublishedRevisionId(Long previousPublishedRevisionId) { this.previousPublishedRevisionId = previousPublishedRevisionId; }
}
