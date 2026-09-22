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
import com.ruoyi.site.domain.SiteCredential;
import com.ruoyi.site.domain.SiteCredentialDraft;
import com.ruoyi.site.domain.SitePublicationRequest;
import com.ruoyi.site.service.ISiteCredentialAdminService;

@Tag(name = "后台管理-企业官网资质证书")
@RestController
@RequestMapping("/site/credential")
public class SiteCredentialController extends BaseController
{
    @Autowired
    private ISiteCredentialAdminService credentialService;

    @Operation(summary = "查询官网资质证书列表")
    @PreAuthorize("@ss.hasPermi('site:credential:list')")
    @GetMapping("/list")
    public TableDataInfo list(SiteCredential credential)
    {
        startPage();
        List<SiteCredential> list = credentialService.selectSiteCredentialList(credential);
        return getDataTable(list);
    }

    @Operation(summary = "查询官网资质证书草稿")
    @PreAuthorize("@ss.hasPermi('site:credential:query')")
    @GetMapping("/{credentialId}")
    public AjaxResult detail(@PathVariable Long credentialId)
    {
        SiteCredentialDraft draft = credentialService.selectCredentialDraftById(credentialId);
        return draft == null ? error("资质证书不存在或已删除") : success(draft);
    }

    @Operation(summary = "新建官网资质证书草稿")
    @PreAuthorize("@ss.hasPermi('site:credential:add')")
    @Log(title = "企业官网资质证书", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SiteCredentialDraft draft)
    {
        draft.setCreateBy(getUsername());
        return success(credentialService.saveDraft(draft));
    }

    @Operation(summary = "保存官网资质证书草稿")
    @PreAuthorize("@ss.hasPermi('site:credential:edit')")
    @Log(title = "企业官网资质证书", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SiteCredentialDraft draft)
    {
        draft.setUpdateBy(getUsername());
        return success(credentialService.saveDraft(draft));
    }

    @Operation(summary = "直接发布官网资质证书草稿")
    @PreAuthorize("@ss.hasPermi('site:credential:publish')")
    @Log(title = "企业官网资质证书", businessType = BusinessType.UPDATE)
    @PostMapping("/{credentialId}/publish")
    public AjaxResult publish(@PathVariable Long credentialId, @RequestBody(required = false) SitePublicationRequest request)
    {
        credentialService.publishCredential(credentialId, getUsername(), request == null ? null : request.getReason());
        return success();
    }

    @Operation(summary = "下架官网资质证书")
    @PreAuthorize("@ss.hasPermi('site:credential:publish')")
    @Log(title = "企业官网资质证书", businessType = BusinessType.UPDATE)
    @PostMapping("/{credentialId}/unpublish")
    public AjaxResult unpublish(@PathVariable Long credentialId, @RequestBody(required = false) SitePublicationRequest request)
    {
        credentialService.unpublishCredential(credentialId, getUsername(), request == null ? null : request.getReason());
        return success();
    }
}
