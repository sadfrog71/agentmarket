package com.ruoyi.site.service;

import java.util.List;
import com.ruoyi.site.domain.SitePublicCredential;

public interface ISiteCredentialService
{
    List<SitePublicCredential> selectPublishedCredentialList(String credentialType, int limit);
}
