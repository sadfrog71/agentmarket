package com.ruoyi.market.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 智能体市场产品。
 */
public class BizAgent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long agentId;
    private String agentCode;
    private String agentName;
    private String primaryCategoryCode;
    private String categoryCode;
    private String iconCode;
    private String coverUrl;
    private String demoUrl;
    private Long providerId;
    private String providerName;
    private String summary;
    private String description;
    private String priceText;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private String certLevel;
    private BigDecimal rating;
    private Integer deployCount;
    private String deliveryCycle;
    private String serviceMode;
    private String recommendFlag;
    private Integer hotScore;
    private Integer sortNo;
    private String publishStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishedAt;
    private String slug;
    private Integer versionNo;
    private String extJson;
    private String delFlag;
    private List<BizAgentDetailItem> detailItems;

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
    @NotBlank(message = "智能体编码不能为空")
    @Size(max = 64, message = "智能体编码不能超过64个字符")
    public String getAgentCode() { return agentCode; }
    public void setAgentCode(String agentCode) { this.agentCode = agentCode; }
    @NotBlank(message = "智能体名称不能为空")
    @Size(max = 100, message = "智能体名称不能超过100个字符")
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    @NotBlank(message = "一级分类不能为空")
    public String getPrimaryCategoryCode() { return primaryCategoryCode; }
    public void setPrimaryCategoryCode(String primaryCategoryCode) { this.primaryCategoryCode = primaryCategoryCode; }
    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
    public String getIconCode() { return iconCode; }
    public void setIconCode(String iconCode) { this.iconCode = iconCode; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    @Size(max = 500, message = "演示环境地址不能超过500个字符")
    public String getDemoUrl() { return demoUrl; }
    public void setDemoUrl(String demoUrl) { this.demoUrl = demoUrl; }
    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPriceText() { return priceText; }
    public void setPriceText(String priceText) { this.priceText = priceText; }
    public BigDecimal getPriceMin() { return priceMin; }
    public void setPriceMin(BigDecimal priceMin) { this.priceMin = priceMin; }
    public BigDecimal getPriceMax() { return priceMax; }
    public void setPriceMax(BigDecimal priceMax) { this.priceMax = priceMax; }
    public String getCertLevel() { return certLevel; }
    public void setCertLevel(String certLevel) { this.certLevel = certLevel; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public Integer getDeployCount() { return deployCount; }
    public void setDeployCount(Integer deployCount) { this.deployCount = deployCount; }
    public String getDeliveryCycle() { return deliveryCycle; }
    public void setDeliveryCycle(String deliveryCycle) { this.deliveryCycle = deliveryCycle; }
    public String getServiceMode() { return serviceMode; }
    public void setServiceMode(String serviceMode) { this.serviceMode = serviceMode; }
    public String getRecommendFlag() { return recommendFlag; }
    public void setRecommendFlag(String recommendFlag) { this.recommendFlag = recommendFlag; }
    public Integer getHotScore() { return hotScore; }
    public void setHotScore(Integer hotScore) { this.hotScore = hotScore; }
    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
    public String getPublishStatus() { return publishStatus; }
    public void setPublishStatus(String publishStatus) { this.publishStatus = publishStatus; }
    public Date getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Date publishedAt) { this.publishedAt = publishedAt; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public Integer getVersionNo() { return versionNo; }
    public void setVersionNo(Integer versionNo) { this.versionNo = versionNo; }
    public String getExtJson() { return extJson; }
    public void setExtJson(String extJson) { this.extJson = extJson; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public List<BizAgentDetailItem> getDetailItems() { return detailItems; }
    public void setDetailItems(List<BizAgentDetailItem> detailItems) { this.detailItems = detailItems; }
}
