package com.ruoyi.site.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.site.domain.SitePublicMedia;
import com.ruoyi.site.mapper.SiteMediaMapper;
import com.ruoyi.site.service.ISiteMediaService;

@Service
public class SiteMediaServiceImpl implements ISiteMediaService
{
    @Autowired
    private SiteMediaMapper mediaMapper;

    @Override
    public SitePublicMedia selectPublishedMedia(String publicId)
    {
        if (StringUtils.isEmpty(publicId) || !publicId.matches("[0-9a-fA-F-]{36}"))
        {
            return null;
        }
        return mediaMapper.selectPublishedMediaByPublicId(publicId);
    }

    @Override
    public Resource readPublishedMedia(SitePublicMedia media) throws IOException
    {
        if (media == null || StringUtils.isEmpty(media.getStorageKey()))
        {
            throw new ServiceException("官网媒体不存在或未发布");
        }
        Path basePath = Path.of(RuoYiConfig.getSiteMediaPath()).toAbsolutePath().normalize();
        Path target = basePath.resolve(media.getStorageKey()).normalize();
        if (!target.startsWith(basePath) || !Files.isRegularFile(target))
        {
            throw new ServiceException("官网媒体文件不存在或不可读取");
        }
        return new FileSystemResource(target);
    }
}
