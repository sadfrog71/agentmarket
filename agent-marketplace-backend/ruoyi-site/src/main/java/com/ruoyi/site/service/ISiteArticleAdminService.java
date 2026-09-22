package com.ruoyi.site.service;

import java.util.List;
import com.ruoyi.site.domain.SiteArticle;
import com.ruoyi.site.domain.SiteArticleDraft;

public interface ISiteArticleAdminService
{
    List<SiteArticle> selectSiteArticleList(SiteArticle article);

    SiteArticleDraft selectArticleDraftById(Long articleId);

    SiteArticleDraft saveDraft(SiteArticleDraft draft);

    void publishArticle(Long articleId, String operator, String reason);

    void unpublishArticle(Long articleId, String operator, String reason);
}
