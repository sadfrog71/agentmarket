package com.ruoyi.web.controller.open;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.site.domain.SitePublicCredential;
import com.ruoyi.site.service.ISiteCredentialService;

@Anonymous
@Tag(name = "前台公开接口-企业官网资质证书")
@RestController
@RequestMapping("/open/site/v1/credentials")
public class OpenCorporateSiteCredentialController extends BaseController
{
    @Autowired
    private ISiteCredentialService credentialService;

    @Operation(summary = "查询已发布官网资质证书")
    @GetMapping
    public AjaxResult list(@RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "limit", defaultValue = "100") int limit, HttpServletResponse response)
    {
        response.setHeader("Cache-Control", "no-store");
        List<SitePublicCredential> credentials = credentialService.selectPublishedCredentialList(type, limit);
        return success(credentials);
    }
}
