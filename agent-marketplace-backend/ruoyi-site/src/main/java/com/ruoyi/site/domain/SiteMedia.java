package com.ruoyi.site.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 官网媒体。stagingPath 绝不经公开静态资源路径暴露，只有 AVAILABLE 才可由公开媒体端点读取。
 */
public class SiteMedia extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long mediaId;
    private String publicId;
    private String mediaKind;
    private String originalFilename;
    private String sourceLegacyKey;
    private String sourceSha256;
    private String contentSha256;
    private String mimeType;
    private Long fileSize;
    private String mediaState;
    private String stagingPath;
    private String storageKey;
    private String publicPath;

    public Long getMediaId() { return mediaId; }
    public void setMediaId(Long mediaId) { this.mediaId = mediaId; }
    public String getPublicId() { return publicId; }
    public void setPublicId(String publicId) { this.publicId = publicId; }
    public String getMediaKind() { return mediaKind; }
    public void setMediaKind(String mediaKind) { this.mediaKind = mediaKind; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public String getSourceLegacyKey() { return sourceLegacyKey; }
    public void setSourceLegacyKey(String sourceLegacyKey) { this.sourceLegacyKey = sourceLegacyKey; }
    public String getSourceSha256() { return sourceSha256; }
    public void setSourceSha256(String sourceSha256) { this.sourceSha256 = sourceSha256; }
    public String getContentSha256() { return contentSha256; }
    public void setContentSha256(String contentSha256) { this.contentSha256 = contentSha256; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getMediaState() { return mediaState; }
    public void setMediaState(String mediaState) { this.mediaState = mediaState; }
    public String getStagingPath() { return stagingPath; }
    public void setStagingPath(String stagingPath) { this.stagingPath = stagingPath; }
    public String getStorageKey() { return storageKey; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }
    public String getPublicPath() { return publicPath; }
    public void setPublicPath(String publicPath) { this.publicPath = publicPath; }
}
