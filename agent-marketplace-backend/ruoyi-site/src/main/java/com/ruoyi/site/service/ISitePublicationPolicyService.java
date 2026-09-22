package com.ruoyi.site.service;

import com.ruoyi.site.domain.SitePage;
import com.ruoyi.site.domain.SitePageRevision;

/**
 * 发布前的领域规则。实际发布服务需要在同一数据库事务中调用本规则、锁定根对象、核验媒体引用后切换指针。
 */
public interface ISitePublicationPolicyService
{
    void validatePagePublish(SitePage page, SitePageRevision draftRevision);
}
