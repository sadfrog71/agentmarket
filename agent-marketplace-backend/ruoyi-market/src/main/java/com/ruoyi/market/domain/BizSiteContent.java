package com.ruoyi.market.domain;

import jakarta.validation.constraints.NotBlank;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 前台页面内容。
 */
public class BizSiteContent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long contentId;
    private String contentKey;
    private String contentName;
    private String title;
    private String subtitle;
    private String content;
    private String contentFormat;
    private String publishStatus;
    private Integer sortNo;
    private String extJson;
    private String delFlag;

    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }
    @NotBlank(message = "内容标识不能为空")
    public String getContentKey() { return contentKey; }
    public void setContentKey(String contentKey) { this.contentKey = contentKey; }
    @NotBlank(message = "内容名称不能为空")
    public String getContentName() { return contentName; }
    public void setContentName(String contentName) { this.contentName = contentName; }
    @NotBlank(message = "页面标题不能为空")
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContentFormat() { return contentFormat; }
    public void setContentFormat(String contentFormat) { this.contentFormat = contentFormat; }
    public String getPublishStatus() { return publishStatus; }
    public void setPublishStatus(String publishStatus) { this.publishStatus = publishStatus; }
    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
    public String getExtJson() { return extJson; }
    public void setExtJson(String extJson) { this.extJson = extJson; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
