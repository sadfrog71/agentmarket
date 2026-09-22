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
import com.ruoyi.site.domain.SiteArticle;
import com.ruoyi.site.domain.SiteArticleDraft;
import com.ruoyi.site.domain.SitePublicationRequest;
import com.ruoyi.site.service.ISiteArticleAdminService;

@Tag(name = "后台管理-企业官网新闻")
@RestController
@RequestMapping("/site/article")
public class SiteArticleController extends BaseController
{
    @Autowired
    private ISiteArticleAdminService articleService;

    @Operation(summary = "查询官网新闻列表")
    @PreAuthorize("@ss.hasPermi('site:article:list')")
    @GetMapping("/list")
    public TableDataInfo list(SiteArticle article)
    {
        startPage();
        List<SiteArticle> list = articleService.selectSiteArticleList(article);
        return getDataTable(list);
    }

    @Operation(summary = "查询官网新闻草稿")
    @PreAuthorize("@ss.hasPermi('site:article:query')")
    @GetMapping("/{articleId}")
    public AjaxResult detail(@PathVariable Long articleId)
    {
        SiteArticleDraft draft = articleService.selectArticleDraftById(articleId);
        return draft == null ? error("新闻不存在或已删除") : success(draft);
    }

    @Operation(summary = "新建官网新闻草稿")
    @PreAuthorize("@ss.hasPermi('site:article:add')")
    @Log(title = "企业官网新闻", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SiteArticleDraft draft)
    {
        draft.setCreateBy(getUsername());
        return success(articleService.saveDraft(draft));
    }

    @Operation(summary = "保存官网新闻草稿")
    @PreAuthorize("@ss.hasPermi('site:article:edit')")
    @Log(title = "企业官网新闻", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SiteArticleDraft draft)
    {
        draft.setUpdateBy(getUsername());
        return success(articleService.saveDraft(draft));
    }

    @Operation(summary = "直接发布官网新闻草稿")
    @PreAuthorize("@ss.hasPermi('site:article:publish')")
    @Log(title = "企业官网新闻", businessType = BusinessType.UPDATE)
    @PostMapping("/{articleId}/publish")
    public AjaxResult publish(@PathVariable Long articleId, @RequestBody(required = false) SitePublicationRequest request)
    {
        articleService.publishArticle(articleId, getUsername(), request == null ? null : request.getReason());
        return success();
    }

    @Operation(summary = "下架官网新闻")
    @PreAuthorize("@ss.hasPermi('site:article:publish')")
    @Log(title = "企业官网新闻", businessType = BusinessType.UPDATE)
    @PostMapping("/{articleId}/unpublish")
    public AjaxResult unpublish(@PathVariable Long articleId, @RequestBody(required = false) SitePublicationRequest request)
    {
        articleService.unpublishArticle(articleId, getUsername(), request == null ? null : request.getReason());
        return success();
    }
}
