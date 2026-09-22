package com.ruoyi.site.domain;

/** 匿名官网资质证书列表使用的显式 DTO。 */
public class SitePublicCredential
{
    private String credentialCode;
    private String credentialType;
    private String title;
    private String documentUrl;

    public String getCredentialCode() { return credentialCode; }
    public void setCredentialCode(String credentialCode) { this.credentialCode = credentialCode; }
    public String getCredentialType() { return credentialType; }
    public void setCredentialType(String credentialType) { this.credentialType = credentialType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
}
