package com.ruoyi.site.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.site.domain.SiteMedia;

public interface ISiteMediaAdminService
{
    List<SiteMedia> selectSiteMediaList(SiteMedia media);

    SiteMedia uploadAvailableMedia(MultipartFile file, String operator) throws IOException;
}
