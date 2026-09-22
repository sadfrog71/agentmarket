package com.ruoyi.site.service;

import java.io.IOException;
import org.springframework.core.io.Resource;
import com.ruoyi.site.domain.SitePublicMedia;

public interface ISiteMediaService
{
    SitePublicMedia selectPublishedMedia(String publicId);

    Resource readPublishedMedia(SitePublicMedia media) throws IOException;
}
