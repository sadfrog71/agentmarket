package com.ruoyi.market.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 智能体详情结构项。
 */
public class BizAgentDetailItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long itemId;
    private Long agentId;
    private String itemType;
    private String title;
    private String subtitle;
    private String valueText;
    private String content;
    private String iconCode;
    private String linkUrl;
    private Integer sortNo;
    private String status;
    private String extJson;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getValueText() { return valueText; }
    public void setValueText(String valueText) { this.valueText = valueText; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getIconCode() { return iconCode; }
    public void setIconCode(String iconCode) { this.iconCode = iconCode; }
    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }
    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getExtJson() { return extJson; }
    public void setExtJson(String extJson) { this.extJson = extJson; }
}
