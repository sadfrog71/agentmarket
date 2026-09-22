package com.ruoyi.site.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.site.domain.SitePublicArticle;
import com.ruoyi.site.mapper.SiteArticleMapper;
import com.ruoyi.site.service.ISiteArticleService;

@Service
public class SiteArticleServiceImpl implements ISiteArticleService
{
    private static final int MAX_PUBLIC_LIST_SIZE = 100;

    @Autowired
    private SiteArticleMapper articleMapper;

    @Override
    public List<SitePublicArticle> selectPublishedArticleList(String categoryCode, int limit)
    {
        String safeCategory = StringUtils.isEmpty(categoryCode) ? null : categoryCode.trim();
        if (safeCategory != null && !safeCategory.matches("[a-zA-Z0-9_-]{1,100}"))
        {
            safeCategory = null;
        }
        return articleMapper.selectPublishedArticleList(safeCategory, Math.max(1, Math.min(limit, MAX_PUBLIC_LIST_SIZE)));
    }
}
