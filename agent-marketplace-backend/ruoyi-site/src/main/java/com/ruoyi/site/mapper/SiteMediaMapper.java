package com.ruoyi.site.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.site.domain.SiteMedia;
import com.ruoyi.site.domain.SitePublicMedia;

public interface SiteMediaMapper
{
    SitePublicMedia selectPublishedMediaByPublicId(@Param("publicId") String publicId);

    List<SiteMedia> selectSiteMediaList(SiteMedia media);

    int insertSiteMedia(SiteMedia media);

    Long selectAvailableMediaIdByPublicId(@Param("publicId") String publicId);

    int deleteMediaReferences(@Param("ownerType") String ownerType, @Param("ownerId") Long ownerId);

    int insertMediaReference(@Param("mediaId") Long mediaId, @Param("ownerType") String ownerType,
            @Param("ownerId") Long ownerId, @Param("referenceRole") String referenceRole,
            @Param("fieldKey") String fieldKey, @Param("sortNo") int sortNo);
}
