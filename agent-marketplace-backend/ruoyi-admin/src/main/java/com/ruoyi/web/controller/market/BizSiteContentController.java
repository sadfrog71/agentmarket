package com.ruoyi.web.controller.market;

import java.util.List;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.market.domain.BizSiteContent;
import com.ruoyi.market.service.IBizSiteContentService;

@Tag(name = "后台管理-页面内容")
@RestController
@RequestMapping("/market/content")
public class BizSiteContentController extends BaseController
{
    @Autowired
    private IBizSiteContentService siteContentService;

    @Operation(summary = "查询页面内容列表")
    @PreAuthorize("@ss.hasPermi('market:content:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizSiteContent content)
    {
        startPage();
        List<BizSiteContent> list = siteContentService.selectSiteContentList(content);
        return getDataTable(list);
    }

    @Operation(summary = "查询页面内容详情")
    @PreAuthorize("@ss.hasPermi('market:content:query')")
    @GetMapping("/{contentId}")
    public AjaxResult getInfo(@PathVariable Long contentId)
    {
        return success(siteContentService.selectSiteContentById(contentId));
    }

    @Operation(summary = "新增页面内容")
    @PreAuthorize("@ss.hasPermi('market:content:add')")
    @Log(title = "页面内容", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Valid @RequestBody BizSiteContent content)
    {
        content.setCreateBy(getUsername());
        return toAjax(siteContentService.insertSiteContent(content));
    }

    @Operation(summary = "更新页面内容")
    @PreAuthorize("@ss.hasPermi('market:content:edit')")
    @Log(title = "页面内容", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Valid @RequestBody BizSiteContent content)
    {
        content.setUpdateBy(getUsername());
        return toAjax(siteContentService.updateSiteContent(content));
    }

    @Operation(summary = "删除页面内容")
    @PreAuthorize("@ss.hasPermi('market:content:remove')")
    @Log(title = "页面内容", businessType = BusinessType.DELETE)
    @DeleteMapping("/{contentIds}")
    public AjaxResult remove(@PathVariable Long[] contentIds)
    {
        return toAjax(siteContentService.deleteSiteContentByIds(contentIds));
    }
}
