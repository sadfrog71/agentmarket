package com.ruoyi.web.controller.open;

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
import com.ruoyi.site.domain.SitePublicPage;
import com.ruoyi.site.service.ISitePageService;

@Anonymous
@Tag(name = "前台公开接口-企业官网页面")
@RestController
@RequestMapping("/open/site/v1/pages")
public class OpenCorporateSitePageController extends BaseController
{
    @Autowired
    private ISitePageService pageService;

    @Operation(summary = "按既有公开路径查询已发布官网页面")
    @GetMapping
    public AjaxResult detail(@RequestParam("path") String path, HttpServletResponse response)
    {
        response.setHeader("Cache-Control", "no-store");
        SitePublicPage page = pageService.selectPublishedPageByRoute(path);
        return page == null ? error("官网页面不存在或未发布") : success(page);
    }
}
