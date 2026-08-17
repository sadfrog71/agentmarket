package com.ruoyi.web.controller.market;

import java.util.List;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.ruoyi.market.domain.BizAgentCategory;
import com.ruoyi.market.service.IBizAgentCategoryService;

@Tag(name = "后台管理-智能体一级分类")
@RestController
@RequestMapping("/market/category")
public class BizAgentCategoryController extends BaseController
{
    @Autowired
    private IBizAgentCategoryService categoryService;

    @Operation(summary = "查询一级分类列表")
    @PreAuthorize("@ss.hasPermi('market:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizAgentCategory category)
    {
        startPage();
        List<BizAgentCategory> list = categoryService.selectCategoryList(category);
        return getDataTable(list);
    }

    @Operation(summary = "查询启用的一级分类选项")
    @PreAuthorize("@ss.hasPermi('market:category:list')")
    @GetMapping("/options")
    public AjaxResult options()
    {
        return success(categoryService.selectEnabledCategoryList());
    }

    @Operation(summary = "查询一级分类详情")
    @PreAuthorize("@ss.hasPermi('market:category:query')")
    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId)
    {
        return success(categoryService.selectCategoryById(categoryId));
    }

    @Operation(summary = "新增一级分类")
    @PreAuthorize("@ss.hasPermi('market:category:add')")
    @Log(title = "智能体一级分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Valid @RequestBody BizAgentCategory category)
    {
        category.setCreateBy(getUsername());
        return toAjax(categoryService.insertCategory(category));
    }

    @Operation(summary = "更新一级分类")
    @PreAuthorize("@ss.hasPermi('market:category:edit')")
    @Log(title = "智能体一级分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Valid @RequestBody BizAgentCategory category)
    {
        category.setUpdateBy(getUsername());
        return toAjax(categoryService.updateCategory(category));
    }

    @Operation(summary = "删除一级分类")
    @PreAuthorize("@ss.hasPermi('market:category:remove')")
    @Log(title = "智能体一级分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        return toAjax(categoryService.deleteCategoryByIds(categoryIds));
    }
}
