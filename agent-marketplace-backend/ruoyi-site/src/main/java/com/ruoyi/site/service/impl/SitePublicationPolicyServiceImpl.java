package com.ruoyi.site.service.impl;

import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.site.domain.SitePage;
import com.ruoyi.site.domain.SitePageRevision;
import com.ruoyi.site.domain.SitePublicationState;
import com.ruoyi.site.service.ISitePublicationPolicyService;

@Service
public class SitePublicationPolicyServiceImpl implements ISitePublicationPolicyService
{
    @Override
    public void validatePagePublish(SitePage page, SitePageRevision draftRevision)
    {
        if (page == null || draftRevision == null)
        {
            throw new ServiceException("页面和待发布修订不能为空");
        }
        if (!page.getPageId().equals(draftRevision.getPageId()))
        {
            throw new ServiceException("待发布修订不属于当前页面");
        }
        if (!SitePublicationState.DRAFT.name().equals(draftRevision.getRevisionState()))
        {
            throw new ServiceException("只有草稿修订可以发布");
        }
        if (draftRevision.getTitle() == null || draftRevision.getTitle().isBlank())
        {
            throw new ServiceException("页面标题不能为空");
        }
    }
}
