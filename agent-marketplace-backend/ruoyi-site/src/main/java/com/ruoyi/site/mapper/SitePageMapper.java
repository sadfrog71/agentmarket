package com.ruoyi.site.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.site.domain.SitePage;
import com.ruoyi.site.domain.SitePageRevision;
import com.ruoyi.site.domain.SitePublicPage;

/**
 * 页面根对象的数据访问契约。发布服务以 selectForUpdate 锁定根对象后再原子切换指针。
 */
public interface SitePageMapper
{
    List<SitePage> selectSitePageList(SitePage page);

    SitePage selectSitePageById(Long pageId);

    SitePage selectSitePageForUpdate(Long pageId);

    int updateRevisionPointers(SitePage page);

    int insertSitePage(SitePage page);

    int updateSitePage(SitePage page);

    SitePageRevision selectRevisionById(Long revisionId);

    SitePageRevision selectCurrentDraftByPageId(Long pageId);

    int selectNextRevisionNo(Long pageId);

    int insertPageRevision(SitePageRevision revision);

    int updateDraftPageRevision(SitePageRevision revision);

    int updateRevisionState(@Param("revisionId") Long revisionId, @Param("revisionState") String revisionState,
            @Param("publishedBy") String publishedBy);

    int insertPublishAudit(@Param("aggregateId") Long aggregateId, @Param("actionType") String actionType,
            @Param("fromRevisionId") Long fromRevisionId, @Param("toRevisionId") Long toRevisionId,
            @Param("operator") String operator, @Param("reason") String reason);

    SitePublicPage selectPublishedPageByRoute(@Param("routePath") String routePath);
}
