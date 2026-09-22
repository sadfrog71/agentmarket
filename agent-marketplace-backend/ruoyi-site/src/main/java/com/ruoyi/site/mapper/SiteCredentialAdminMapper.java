package com.ruoyi.site.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.site.domain.SiteCredential;
import com.ruoyi.site.domain.SiteCredentialRevision;

public interface SiteCredentialAdminMapper
{
    List<SiteCredential> selectSiteCredentialList(SiteCredential credential);
    SiteCredential selectSiteCredentialById(Long credentialId);
    SiteCredential selectSiteCredentialForUpdate(Long credentialId);
    int insertSiteCredential(SiteCredential credential);
    int updateSiteCredential(SiteCredential credential);
    int updateRevisionPointers(SiteCredential credential);
    SiteCredentialRevision selectRevisionById(Long revisionId);
    SiteCredentialRevision selectCurrentDraftByCredentialId(Long credentialId);
    int selectNextRevisionNo(Long credentialId);
    int insertCredentialRevision(SiteCredentialRevision revision);
    int updateDraftCredentialRevision(SiteCredentialRevision revision);
    int updateRevisionState(@Param("revisionId") Long revisionId, @Param("revisionState") String revisionState, @Param("publishedBy") String publishedBy);
    int insertPublishAudit(@Param("aggregateId") Long aggregateId, @Param("actionType") String actionType, @Param("fromRevisionId") Long fromRevisionId, @Param("toRevisionId") Long toRevisionId, @Param("operator") String operator, @Param("reason") String reason);
}
