package com.ruoyi.web.controller.open;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.market.domain.BizSiteContent;
import com.ruoyi.market.service.IBizSiteContentService;

@Anonymous
@Tag(name = "前台公开接口-页面内容")
@RestController
@RequestMapping("/open/content")
public class OpenSiteContentController extends BaseController
{
    @Autowired
    private IBizSiteContentService siteContentService;

    @Operation(summary = "按标识查询已发布页面内容")
    @GetMapping("/{contentKey}")
    public AjaxResult detail(@PathVariable String contentKey)
    {
        BizSiteContent content = siteContentService.selectPublishedSiteContentByKey(contentKey.toUpperCase());
        return content == null ? error("页面内容不存在或未发布") : success(content);
    }
}
