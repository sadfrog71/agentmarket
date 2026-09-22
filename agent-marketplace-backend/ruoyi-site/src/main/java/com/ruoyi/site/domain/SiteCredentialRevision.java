package com.ruoyi.site.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/** 资质文件的不可变修订。 */
public class SiteCredentialRevision extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long revisionId;
    private Long credentialId;
    private Integer revisionNo;
    private String revisionState;
    private String title;
    private Long documentMediaId;
    private String documentPublicId;
    private String contentHash;
    private String publishedBy;

    public Long getRevisionId() { return revisionId; }
    public void setRevisionId(Long revisionId) { this.revisionId = revisionId; }
    public Long getCredentialId() { return credentialId; }
    public void setCredentialId(Long credentialId) { this.credentialId = credentialId; }
    public Integer getRevisionNo() { return revisionNo; }
    public void setRevisionNo(Integer revisionNo) { this.revisionNo = revisionNo; }
    public String getRevisionState() { return revisionState; }
    public void setRevisionState(String revisionState) { this.revisionState = revisionState; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getDocumentMediaId() { return documentMediaId; }
    public void setDocumentMediaId(Long documentMediaId) { this.documentMediaId = documentMediaId; }
    public String getDocumentPublicId() { return documentPublicId; }
    public void setDocumentPublicId(String documentPublicId) { this.documentPublicId = documentPublicId; }
    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }
    public String getPublishedBy() { return publishedBy; }
    public void setPublishedBy(String publishedBy) { this.publishedBy = publishedBy; }
}
