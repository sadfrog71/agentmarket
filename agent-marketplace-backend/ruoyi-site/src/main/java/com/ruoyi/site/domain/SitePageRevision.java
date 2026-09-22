package com.ruoyi.site.domain;

import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 页面不可变发布修订，区块与条目仅归属一个页面修订。
 */
public class SitePageRevision extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long revisionId;
    private Long pageId;
    private Integer revisionNo;
    private String revisionState;
    private String title;
    private String subtitle;
    private String bodyHtml;
    private String seoJson;
    private String pageDataJson;
    private String contentHash;
    private Date publishedAt;
    private String publishedBy;

    public Long getRevisionId() { return revisionId; }
    public void setRevisionId(Long revisionId) { this.revisionId = revisionId; }
    public Long getPageId() { return pageId; }
    public void setPageId(Long pageId) { this.pageId = pageId; }
    public Integer getRevisionNo() { return revisionNo; }
    public void setRevisionNo(Integer revisionNo) { this.revisionNo = revisionNo; }
    public String getRevisionState() { return revisionState; }
    public void setRevisionState(String revisionState) { this.revisionState = revisionState; }
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
    public String getPublishedBy() { return publishedBy; }
    public void setPublishedBy(String publishedBy) { this.publishedBy = publishedBy; }
}
