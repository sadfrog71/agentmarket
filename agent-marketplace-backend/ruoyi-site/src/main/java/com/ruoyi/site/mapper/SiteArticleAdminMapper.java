package com.ruoyi.site.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.site.domain.SiteArticle;
import com.ruoyi.site.domain.SiteArticleRevision;

public interface SiteArticleAdminMapper
{
    List<SiteArticle> selectSiteArticleList(SiteArticle article);

    SiteArticle selectSiteArticleById(Long articleId);

    SiteArticle selectSiteArticleForUpdate(Long articleId);

    Long selectCategoryIdByCode(@Param("categoryCode") String categoryCode);

    int insertSiteArticle(SiteArticle article);

    int updateSiteArticle(SiteArticle article);

    int updateRevisionPointers(SiteArticle article);

    SiteArticleRevision selectRevisionById(Long revisionId);

    SiteArticleRevision selectCurrentDraftByArticleId(Long articleId);

    int selectNextRevisionNo(Long articleId);

    int insertArticleRevision(SiteArticleRevision revision);

    int updateDraftArticleRevision(SiteArticleRevision revision);

    int updateRevisionState(@Param("revisionId") Long revisionId, @Param("revisionState") String revisionState,
            @Param("publishedBy") String publishedBy);

    int insertPublishAudit(@Param("aggregateId") Long aggregateId, @Param("actionType") String actionType,
            @Param("fromRevisionId") Long fromRevisionId, @Param("toRevisionId") Long toRevisionId,
            @Param("operator") String operator, @Param("reason") String reason);
}
