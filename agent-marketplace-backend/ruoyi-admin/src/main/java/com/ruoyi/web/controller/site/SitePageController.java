package com.ruoyi.web.controller.site;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ruoyi.site.domain.SitePage;
import com.ruoyi.site.domain.SitePageDraft;
import com.ruoyi.site.domain.SitePublicationRequest;
import com.ruoyi.site.service.ISitePageService;

@Tag(name = "后台管理-企业官网页面")
@RestController
@RequestMapping("/site/page")
public class SitePageController extends BaseController
{
    @Autowired
    private ISitePageService pageService;

    @Operation(summary = "查询企业官网页面列表")
    @PreAuthorize("@ss.hasPermi('site:page:list')")
    @GetMapping("/list")
    public TableDataInfo list(SitePage page)
    {
        startPage();
        List<SitePage> list = pageService.selectSitePageList(page);
        return getDataTable(list);
    }

    @Operation(summary = "查询企业官网页面草稿")
    @PreAuthorize("@ss.hasPermi('site:page:query')")
    @GetMapping("/{pageId}")
    public AjaxResult detail(@PathVariable Long pageId)
    {
        SitePageDraft draft = pageService.selectPageDraftById(pageId);
        return draft == null ? error("官网页面不存在或已删除") : success(draft);
    }

    @Operation(summary = "新建企业官网页面草稿")
    @PreAuthorize("@ss.hasPermi('site:page:add')")
    @Log(title = "企业官网页面", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SitePageDraft draft)
    {
        draft.setCreateBy(getUsername());
        return success(pageService.saveDraft(draft));
    }

    @Operation(summary = "保存企业官网页面草稿")
    @PreAuthorize("@ss.hasPermi('site:page:edit')")
    @Log(title = "企业官网页面", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SitePageDraft draft)
    {
        draft.setUpdateBy(getUsername());
        return success(pageService.saveDraft(draft));
    }

    @Operation(summary = "直接发布企业官网页面草稿")
    @PreAuthorize("@ss.hasPermi('site:page:publish')")
    @Log(title = "企业官网页面", businessType = BusinessType.UPDATE)
    @PostMapping("/{pageId}/publish")
    public AjaxResult publish(@PathVariable Long pageId, @RequestBody(required = false) SitePublicationRequest request)
    {
        pageService.publishPage(pageId, getUsername(), request == null ? null : request.getReason());
        return success();
    }

    @Operation(summary = "下架企业官网页面")
    @PreAuthorize("@ss.hasPermi('site:page:publish')")
    @Log(title = "企业官网页面", businessType = BusinessType.UPDATE)
    @PostMapping("/{pageId}/unpublish")
    public AjaxResult unpublish(@PathVariable Long pageId, @RequestBody(required = false) SitePublicationRequest request)
    {
        pageService.unpublishPage(pageId, getUsername(), request == null ? null : request.getReason());
        return success();
    }
}
