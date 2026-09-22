package com.ruoyi.site.domain;

import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

/** 新闻不可变修订。 */
public class SiteArticleRevision extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long revisionId;
    private Long articleId;
    private Integer revisionNo;
    private String revisionState;
    private String title;
    private String summary;
    private String bodyHtml;
    private String seoJson;
    private String contentHash;
    private Date publishedAt;
    private String publishedBy;

    public Long getRevisionId() { return revisionId; }
    public void setRevisionId(Long revisionId) { this.revisionId = revisionId; }
    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }
    public Integer getRevisionNo() { return revisionNo; }
    public void setRevisionNo(Integer revisionNo) { this.revisionNo = revisionNo; }
    public String getRevisionState() { return revisionState; }
    public void setRevisionState(String revisionState) { this.revisionState = revisionState; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getBodyHtml() { return bodyHtml; }
    public void setBodyHtml(String bodyHtml) { this.bodyHtml = bodyHtml; }
    public String getSeoJson() { return seoJson; }
    public void setSeoJson(String seoJson) { this.seoJson = seoJson; }
    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }
    public Date getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Date publishedAt) { this.publishedAt = publishedAt; }
    public String getPublishedBy() { return publishedBy; }
    public void setPublishedBy(String publishedBy) { this.publishedBy = publishedBy; }
}
