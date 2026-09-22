package com.ruoyi.site.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.site.domain.SitePublicCredential;
import com.ruoyi.site.mapper.SiteCredentialMapper;
import com.ruoyi.site.service.ISiteCredentialService;

@Service
public class SiteCredentialServiceImpl implements ISiteCredentialService
{
    private static final int MAX_PUBLIC_LIST_SIZE = 200;

    @Autowired
    private SiteCredentialMapper credentialMapper;

    @Override
    public List<SitePublicCredential> selectPublishedCredentialList(String credentialType, int limit)
    {
        String safeType = StringUtils.isEmpty(credentialType) ? null : credentialType.trim();
        if (safeType != null && !safeType.matches("[A-Z_]{1,32}"))
        {
            safeType = null;
        }
        return credentialMapper.selectPublishedCredentialList(safeType, Math.max(1, Math.min(limit, MAX_PUBLIC_LIST_SIZE)));
    }
}
