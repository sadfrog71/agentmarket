package com.ruoyi.site.service.impl;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.site.mapper.SiteMediaMapper;

/** Keeps explicit published-media authorization aligned with editable revisions. */
@Service
public class SiteMediaReferenceService
{
    private static final Pattern PUBLIC_MEDIA_URL = Pattern.compile(
            "(?i)(?:/api)?/open/site/v1/media/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})");

    @Autowired
    private SiteMediaMapper mediaMapper;

    public void synchronizeHtmlReferences(String ownerType, Long revisionId, String content)
    {
        mediaMapper.deleteMediaReferences(ownerType, revisionId);
        Matcher matcher = PUBLIC_MEDIA_URL.matcher(content == null ? "" : content);
        Set<String> publicIds = new LinkedHashSet<>();
        while (matcher.find())
        {
            publicIds.add(matcher.group(1).toLowerCase());
        }
        int sortNo = 0;
        for (String publicId : publicIds)
        {
            Long mediaId = mediaMapper.selectAvailableMediaIdByPublicId(publicId);
            if (mediaId == null)
            {
                throw new ServiceException("正文引用的媒体不存在、尚未入库或未就绪：" + publicId);
            }
            mediaMapper.insertMediaReference(mediaId, ownerType, revisionId, "EMBED", "content-" + sortNo, sortNo);
            sortNo++;
        }
    }

    public void synchronizeDocumentReference(Long revisionId, Long mediaId)
    {
        mediaMapper.deleteMediaReferences("CREDENTIAL_REVISION", revisionId);
        mediaMapper.insertMediaReference(mediaId, "CREDENTIAL_REVISION", revisionId, "ATTACHMENT", "document", 0);
    }
}
