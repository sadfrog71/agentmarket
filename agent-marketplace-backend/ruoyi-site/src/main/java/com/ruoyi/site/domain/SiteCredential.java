package com.ruoyi.site.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/** 企业官网软件著作权、专利等资质的稳定根对象。 */
public class SiteCredential extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long credentialId;
    private String credentialCode;
    private String credentialType;
    /** 当前草稿优先、否则当前已发布修订的中文证书名称，仅供后台列表展示。 */
    private String title;
    private Long draftRevisionId;
    private Long publishedRevisionId;
    private Long previousPublishedRevisionId;

    public Long getCredentialId() { return credentialId; }
    public void setCredentialId(Long credentialId) { this.credentialId = credentialId; }
    public String getCredentialCode() { return credentialCode; }
    public void setCredentialCode(String credentialCode) { this.credentialCode = credentialCode; }
    public String getCredentialType() { return credentialType; }
    public void setCredentialType(String credentialType) { this.credentialType = credentialType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getDraftRevisionId() { return draftRevisionId; }
    public void setDraftRevisionId(Long draftRevisionId) { this.draftRevisionId = draftRevisionId; }
    public Long getPublishedRevisionId() { return publishedRevisionId; }
    public void setPublishedRevisionId(Long publishedRevisionId) { this.publishedRevisionId = publishedRevisionId; }
    public Long getPreviousPublishedRevisionId() { return previousPublishedRevisionId; }
    public void setPreviousPublishedRevisionId(Long previousPublishedRevisionId) { this.previousPublishedRevisionId = previousPublishedRevisionId; }
}
