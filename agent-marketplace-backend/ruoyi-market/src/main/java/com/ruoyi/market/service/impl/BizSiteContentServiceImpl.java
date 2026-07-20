package com.ruoyi.market.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.market.domain.BizSiteContent;
import com.ruoyi.market.mapper.BizSiteContentMapper;
import com.ruoyi.market.service.IBizSiteContentService;

@Service
public class BizSiteContentServiceImpl implements IBizSiteContentService
{
    @Autowired
    private BizSiteContentMapper siteContentMapper;

    @Override
    public BizSiteContent selectSiteContentById(Long contentId)
    {
        return siteContentMapper.selectSiteContentById(contentId);
    }

    @Override
    public BizSiteContent selectPublishedSiteContentByKey(String contentKey)
    {
        return siteContentMapper.selectPublishedSiteContentByKey(contentKey);
    }

    @Override
    public List<BizSiteContent> selectSiteContentList(BizSiteContent content)
    {
        return siteContentMapper.selectSiteContentList(content);
    }

    @Override
    public int insertSiteContent(BizSiteContent content)
    {
        applyDefaults(content);
        return siteContentMapper.insertSiteContent(content);
    }

    @Override
    public int updateSiteContent(BizSiteContent content)
    {
        applyDefaults(content);
        return siteContentMapper.updateSiteContent(content);
    }

    @Override
    public int deleteSiteContentByIds(Long[] contentIds)
    {
        return siteContentMapper.softDeleteSiteContentByIds(contentIds);
    }

    private void applyDefaults(BizSiteContent content)
    {
        content.setContentKey(content.getContentKey().trim().toUpperCase());
        if (StringUtils.isEmpty(content.getContentFormat()))
        {
            content.setContentFormat("RICH_TEXT");
        }
        if (StringUtils.isEmpty(content.getPublishStatus()))
        {
            content.setPublishStatus("0");
        }
        if (content.getSortNo() == null)
        {
            content.setSortNo(0);
        }
    }
}
