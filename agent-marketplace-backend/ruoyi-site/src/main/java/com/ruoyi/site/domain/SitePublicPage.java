package com.ruoyi.site.domain;

import java.util.Date;

/**
 * 匿名官网接口使用的显式页面 DTO；不携带后台审计、草稿、媒体物理路径等字段。
 */
public class SitePublicPage
{
    private String pageCode;
    private String routePath;
    private String templateCode;
    private String title;
    private String subtitle;
    private String bodyHtml;
    private String seoJson;
    private String pageDataJson;
    private String contentHash;
    private Date publishedAt;

    public String getPageCode() { return pageCode; }
    public void setPageCode(String pageCode) { this.pageCode = pageCode; }
    public String getRoutePath() { return routePath; }
    public void setRoutePath(String routePath) { this.routePath = routePath; }
    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getBodyHtml() { return bodyHtml; }
    public void setBodyHtml(String bodyHtml) { this.bodyHtml = bodyHtml; }
    public String getSeoJson() { return seoJson; }
    public void setSeoJson(String seoJson) { this.seoJson = seoJson; }
    public String getPageDataJson() { return pageDataJson; }
    public void setPageDataJson(String pageDataJson) { this.pageDataJson = pageDataJson; }
    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }
    public Date getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Date publishedAt) { this.publishedAt = publishedAt; }
}
