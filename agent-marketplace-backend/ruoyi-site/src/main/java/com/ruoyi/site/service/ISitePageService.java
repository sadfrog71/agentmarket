package com.ruoyi.site.service;

import java.util.List;
import com.ruoyi.site.domain.SitePage;
import com.ruoyi.site.domain.SitePageDraft;
import com.ruoyi.site.domain.SitePublicPage;

public interface ISitePageService
{
    List<SitePage> selectSitePageList(SitePage page);

    SitePageDraft selectPageDraftById(Long pageId);

    SitePageDraft saveDraft(SitePageDraft draft);

    void publishPage(Long pageId, String operator, String reason);

    void unpublishPage(Long pageId, String operator, String reason);

    SitePublicPage selectPublishedPageByRoute(String routePath);
}
