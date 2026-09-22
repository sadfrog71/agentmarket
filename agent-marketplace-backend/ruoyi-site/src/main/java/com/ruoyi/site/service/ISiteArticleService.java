package com.ruoyi.site.service;

import java.util.List;
import com.ruoyi.site.domain.SitePublicArticle;

public interface ISiteArticleService
{
    List<SitePublicArticle> selectPublishedArticleList(String categoryCode, int limit);
}
