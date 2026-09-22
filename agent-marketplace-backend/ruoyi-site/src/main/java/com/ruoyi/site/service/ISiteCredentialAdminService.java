package com.ruoyi.site.service;

import java.util.List;
import com.ruoyi.site.domain.SiteCredential;
import com.ruoyi.site.domain.SiteCredentialDraft;

public interface ISiteCredentialAdminService
{
    List<SiteCredential> selectSiteCredentialList(SiteCredential credential);
    SiteCredentialDraft selectCredentialDraftById(Long credentialId);
    SiteCredentialDraft saveDraft(SiteCredentialDraft draft);
    void publishCredential(Long credentialId, String operator, String reason);
    void unpublishCredential(Long credentialId, String operator, String reason);
}
