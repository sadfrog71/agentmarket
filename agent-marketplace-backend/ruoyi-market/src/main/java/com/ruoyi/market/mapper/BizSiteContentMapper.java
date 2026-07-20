package com.ruoyi.market.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.market.domain.BizSiteContent;

public interface BizSiteContentMapper
{
    BizSiteContent selectSiteContentById(Long contentId);

    BizSiteContent selectPublishedSiteContentByKey(@Param("contentKey") String contentKey);

    List<BizSiteContent> selectSiteContentList(BizSiteContent content);

    int insertSiteContent(BizSiteContent content);

    int updateSiteContent(BizSiteContent content);

    int softDeleteSiteContentByIds(Long[] contentIds);
}
