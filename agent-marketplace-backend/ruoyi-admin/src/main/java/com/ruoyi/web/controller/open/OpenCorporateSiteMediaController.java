package com.ruoyi.web.controller.open;

import java.io.IOException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.site.domain.SitePublicMedia;
import com.ruoyi.site.service.ISiteMediaService;

@Anonymous
@Tag(name = "前台公开接口-企业官网媒体")
@RestController
@RequestMapping("/open/site/v1/media")
public class OpenCorporateSiteMediaController
{
    @Autowired
    private ISiteMediaService mediaService;

    @Operation(summary = "读取被当前已发布官网内容引用的媒体")
    @GetMapping("/{publicId}")
    public ResponseEntity<Resource> detail(@PathVariable String publicId) throws IOException
    {
        SitePublicMedia media = mediaService.selectPublishedMedia(publicId);
        if (media == null)
        {
            return ResponseEntity.notFound().build();
        }
        Resource resource = mediaService.readPublishedMedia(media);
        MediaType contentType;
        try
        {
            contentType = MediaType.parseMediaType(media.getMimeType());
        }
        catch (IllegalArgumentException ignored)
        {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .contentType(contentType)
                .contentLength(media.getFileSize())
                .body(resource);
    }
}
