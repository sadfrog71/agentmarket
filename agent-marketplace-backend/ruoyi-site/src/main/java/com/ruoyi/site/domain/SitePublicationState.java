package com.ruoyi.site.domain;

/**
 * 官网内容修订状态。
 *
 * <p>公开读取只允许 {@link #PUBLISHED}；管理员编辑始终在 {@link #DRAFT} 修订上进行，
 * 已发布修订不可原地覆盖。</p>
 */
public enum SitePublicationState
{
    DRAFT,
    PUBLISHED,
    SUPERSEDED,
    ARCHIVED;

    /**
     * 判断修订状态是否允许转换，防止发布记录被编辑回草稿或被直接覆写。
     */
    public boolean canTransitionTo(SitePublicationState target)
    {
        if (target == null || this == target)
        {
            return false;
        }
        return switch (this)
        {
            case DRAFT -> target == PUBLISHED || target == ARCHIVED;
            case PUBLISHED -> target == SUPERSEDED || target == ARCHIVED;
            case SUPERSEDED -> target == ARCHIVED;
            case ARCHIVED -> false;
        };
    }
}
