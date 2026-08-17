package com.ruoyi.market.mapper;

import java.util.List;
import com.ruoyi.market.domain.BizAgentCategory;

public interface BizAgentCategoryMapper
{
    BizAgentCategory selectCategoryById(Long categoryId);

    BizAgentCategory selectCategoryByCode(String categoryCode);

    List<BizAgentCategory> selectCategoryList(BizAgentCategory category);

    List<BizAgentCategory> selectEnabledCategoryList();

    int countAgentsByCategoryCode(String categoryCode);

    int insertCategory(BizAgentCategory category);

    int updateCategory(BizAgentCategory category);

    int softDeleteCategoryByIds(Long[] categoryIds);
}
