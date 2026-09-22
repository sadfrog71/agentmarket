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
import com.ruoyi.site.domain.SitePublicArticle;
import com.ruoyi.site.service.ISiteArticleService;

@Anonymous
@Tag(name = "前台公开接口-企业官网新闻")
@RestController
@RequestMapping("/open/site/v1/articles")
public class OpenCorporateSiteArticleController extends BaseController
{
    @Autowired
    private ISiteArticleService articleService;

    @Operation(summary = "查询已发布官网新闻")
    @GetMapping
    public AjaxResult list(@RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "limit", defaultValue = "60") int limit, HttpServletResponse response)
    {
        response.setHeader("Cache-Control", "no-store");
        List<SitePublicArticle> articles = articleService.selectPublishedArticleList(category, limit);
        return success(articles);
    }
}
