package com.ruoyi.site.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.site.domain.SitePublicCredential;

public interface SiteCredentialMapper
{
    List<SitePublicCredential> selectPublishedCredentialList(@Param("credentialType") String credentialType,
            @Param("limit") int limit);
}
