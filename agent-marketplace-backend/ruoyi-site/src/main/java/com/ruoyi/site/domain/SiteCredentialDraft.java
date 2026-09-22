package com.ruoyi.site.domain;

/** 后台编辑资质的受限命令对象。 */
public class SiteCredentialDraft extends SiteCredentialRevision
{
    private static final long serialVersionUID = 1L;

    private Long credentialId;
    private String credentialCode;
    private String credentialType;
    private Long draftRevisionId;
    private Long publishedRevisionId;

    public Long getCredentialId() { return credentialId; }
    public void setCredentialId(Long credentialId) { this.credentialId = credentialId; }
    public String getCredentialCode() { return credentialCode; }
    public void setCredentialCode(String credentialCode) { this.credentialCode = credentialCode; }
    public String getCredentialType() { return credentialType; }
    public void setCredentialType(String credentialType) { this.credentialType = credentialType; }
    public Long getDraftRevisionId() { return draftRevisionId; }
    public void setDraftRevisionId(Long draftRevisionId) { this.draftRevisionId = draftRevisionId; }
    public Long getPublishedRevisionId() { return publishedRevisionId; }
    public void setPublishedRevisionId(Long publishedRevisionId) { this.publishedRevisionId = publishedRevisionId; }
}
