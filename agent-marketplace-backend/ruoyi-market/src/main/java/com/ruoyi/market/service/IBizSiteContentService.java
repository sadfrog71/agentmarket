package com.ruoyi.market.service;

import java.util.List;
import com.ruoyi.market.domain.BizSiteContent;

public interface IBizSiteContentService
{
    BizSiteContent selectSiteContentById(Long contentId);

    BizSiteContent selectPublishedSiteContentByKey(String contentKey);

    List<BizSiteContent> selectSiteContentList(BizSiteContent content);

    int insertSiteContent(BizSiteContent content);

    int updateSiteContent(BizSiteContent content);

    int deleteSiteContentByIds(Long[] contentIds);
}
