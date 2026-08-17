package com.ruoyi.market.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;

/**
 * 智能体部署案例导入行。
 */
public class BizAgentCaseImportRow
{
    @Excel(name = "案例标题", sort = 1, width = 28)
    private String title;

    @Excel(name = "指标值", sort = 2, width = 28)
    private String valueText;

    @Excel(name = "案例说明", sort = 3, width = 60)
    private String content;

    @Excel(name = "排序", sort = 4, cellType = ColumnType.NUMERIC, width = 12)
    private Integer sortNo;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getValueText()
    {
        return valueText;
    }

    public void setValueText(String valueText)
    {
        this.valueText = valueText;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Integer getSortNo()
    {
        return sortNo;
    }

    public void setSortNo(Integer sortNo)
    {
        this.sortNo = sortNo;
    }
}
