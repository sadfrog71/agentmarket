package com.ruoyi.site.domain;

import java.util.Date;

/**
 * 匿名官网新闻列表使用的显式 DTO，不暴露草稿、审计或存储信息。
 */
public class SitePublicArticle
{
    private String articleCode;
    private String legacyPath;
    private String categoryCode;
    private String categoryName;
    private String title;
    private String summary;
    private Date publishedAt;

    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public String getLegacyPath() { return legacyPath; }
    public void setLegacyPath(String legacyPath) { this.legacyPath = legacyPath; }
    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public Date getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Date publishedAt) { this.publishedAt = publishedAt; }
}
