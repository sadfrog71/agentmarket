package com.ruoyi.site.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.site.domain.SitePublicArticle;

public interface SiteArticleMapper
{
    List<SitePublicArticle> selectPublishedArticleList(@Param("categoryCode") String categoryCode,
            @Param("limit") int limit);
}
