package com.ruoyi.web.controller.site;

import java.io.IOException;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.site.domain.SiteMedia;
import com.ruoyi.site.service.ISiteMediaAdminService;

@Tag(name = "后台管理-企业官网媒体")
@RestController
@RequestMapping("/site/media")
public class SiteMediaController extends BaseController
{
    @Autowired
    private ISiteMediaAdminService mediaService;

    @Operation(summary = "查询官网媒体库")
    @PreAuthorize("@ss.hasPermi('site:media:list')")
    @GetMapping("/list")
    public TableDataInfo list(SiteMedia media)
    {
        startPage();
        List<SiteMedia> list = mediaService.selectSiteMediaList(media);
        return getDataTable(list);
    }

    @Operation(summary = "上传官网媒体")
    @PreAuthorize("@ss.hasPermi('site:media:add')")
    @Log(title = "企业官网媒体", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) throws IOException
    {
        return success(mediaService.uploadAvailableMedia(file, getUsername()));
    }
}
