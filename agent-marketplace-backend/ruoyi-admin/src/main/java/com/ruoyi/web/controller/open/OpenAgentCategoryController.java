package com.ruoyi.web.controller.open;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.market.domain.BizAgentCategory;
import com.ruoyi.market.service.IBizAgentCategoryService;

@Anonymous
@Tag(name = "前台公开接口-智能体分类")
@RestController
@RequestMapping("/open/categories")
public class OpenAgentCategoryController
{
    @Autowired
    private IBizAgentCategoryService categoryService;

    @Operation(summary = "查询已启用的智能体一级分类")
    @GetMapping
    public AjaxResult list()
    {
        List<BizAgentCategory> list = categoryService.selectEnabledCategoryList();
        return AjaxResult.success(list);
    }
}
