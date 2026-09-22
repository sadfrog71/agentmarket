package com.ruoyi.site.domain;

/** 公开媒体查询的内部值对象；不向匿名响应输出物理存储键。 */
public class SitePublicMedia
{
    private String mimeType;
    private Long fileSize;
    private String storageKey;

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getStorageKey() { return storageKey; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }
}
