package com.ruoyi.market.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.market.domain.BizAgentCategory;
import com.ruoyi.market.mapper.BizAgentCategoryMapper;
import com.ruoyi.market.service.IBizAgentCategoryService;

@Service
public class BizAgentCategoryServiceImpl implements IBizAgentCategoryService
{
    @Autowired
    private BizAgentCategoryMapper categoryMapper;

    @Override
    public BizAgentCategory selectCategoryById(Long categoryId)
    {
        return categoryMapper.selectCategoryById(categoryId);
    }

    @Override
    public BizAgentCategory selectCategoryByCode(String categoryCode)
    {
        return categoryMapper.selectCategoryByCode(categoryCode);
    }

    @Override
    public List<BizAgentCategory> selectCategoryList(BizAgentCategory category)
    {
        return categoryMapper.selectCategoryList(category);
    }

    @Override
    public List<BizAgentCategory> selectEnabledCategoryList()
    {
        return categoryMapper.selectEnabledCategoryList();
    }

    @Override
    public int insertCategory(BizAgentCategory category)
    {
        normalize(category);
        return categoryMapper.insertCategory(category);
    }

    @Override
    public int updateCategory(BizAgentCategory category)
    {
        normalize(category);
        BizAgentCategory existing = categoryMapper.selectCategoryById(category.getCategoryId());
        if (existing == null)
        {
            throw new IllegalArgumentException("一级分类不存在或已删除");
        }
        if (!existing.getCategoryCode().equals(category.getCategoryCode()))
        {
            throw new IllegalArgumentException("分类编码创建后不可修改");
        }
        return categoryMapper.updateCategory(category);
    }

    @Override
    public int deleteCategoryByIds(Long[] categoryIds)
    {
        for (Long categoryId : categoryIds)
        {
            BizAgentCategory category = categoryMapper.selectCategoryById(categoryId);
            if (category != null && categoryMapper.countAgentsByCategoryCode(category.getCategoryCode()) > 0)
            {
                throw new IllegalArgumentException("分类「" + category.getCategoryName() + "」下仍有关联智能体，不能删除");
            }
        }
        return categoryMapper.softDeleteCategoryByIds(categoryIds);
    }

    private void normalize(BizAgentCategory category)
    {
        category.setCategoryCode(trim(category.getCategoryCode()));
        category.setCategoryName(trim(category.getCategoryName()));
        category.setDescription(trim(category.getDescription()));
        category.setIconCode(StringUtils.isEmpty(trim(category.getIconCode())) ? "water" : trim(category.getIconCode()));
        if (category.getSortNo() == null) category.setSortNo(0);
        if (StringUtils.isEmpty(category.getStatus())) category.setStatus("0");
        if (!"0".equals(category.getStatus()) && !"1".equals(category.getStatus()))
        {
            throw new IllegalArgumentException("分类状态不合法");
        }
    }

    private String trim(String value)
    {
        return value == null ? null : value.trim();
    }
}
