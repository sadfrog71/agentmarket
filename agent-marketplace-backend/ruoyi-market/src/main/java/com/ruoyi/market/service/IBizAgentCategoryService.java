package com.ruoyi.market.service;

import java.util.List;
import com.ruoyi.market.domain.BizAgentCategory;

public interface IBizAgentCategoryService
{
    BizAgentCategory selectCategoryById(Long categoryId);

    BizAgentCategory selectCategoryByCode(String categoryCode);

    List<BizAgentCategory> selectCategoryList(BizAgentCategory category);

    List<BizAgentCategory> selectEnabledCategoryList();

    int insertCategory(BizAgentCategory category);

    int updateCategory(BizAgentCategory category);

    int deleteCategoryByIds(Long[] categoryIds);
}
