package com.ruoyi.market.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 智能体市场一级分类。
 */
public class BizAgentCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long categoryId;
    private String categoryCode;
    private String categoryName;
    private String description;
    private String iconCode;
    private Integer sortNo;
    private String status;
    private String delFlag;

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    @NotBlank(message = "分类编码不能为空")
    @Size(max = 64, message = "分类编码不能超过64个字符")
    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称不能超过100个字符")
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIconCode() { return iconCode; }
    public void setIconCode(String iconCode) { this.iconCode = iconCode; }
    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
