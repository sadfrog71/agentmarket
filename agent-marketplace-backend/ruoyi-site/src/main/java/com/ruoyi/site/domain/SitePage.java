package com.ruoyi.site.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 官网页面根对象。页面本身不保存正文；访客始终通过 publishedRevisionId 读取不可变修订。
 */
public class SitePage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long pageId;
    private String pageCode;
    private String routePath;
    private String templateCode;
    /** 当前草稿优先、否则当前已发布修订的中文页面标题，仅供后台列表展示。 */
    private String title;
    private Long draftRevisionId;
    private Long publishedRevisionId;
    private Long previousPublishedRevisionId;
    private String delFlag;

    public Long getPageId() { return pageId; }
    public void setPageId(Long pageId) { this.pageId = pageId; }
    public String getPageCode() { return pageCode; }
    public void setPageCode(String pageCode) { this.pageCode = pageCode; }
    public String getRoutePath() { return routePath; }
    public void setRoutePath(String routePath) { this.routePath = routePath; }
    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getDraftRevisionId() { return draftRevisionId; }
    public void setDraftRevisionId(Long draftRevisionId) { this.draftRevisionId = draftRevisionId; }
    public Long getPublishedRevisionId() { return publishedRevisionId; }
    public void setPublishedRevisionId(Long publishedRevisionId) { this.publishedRevisionId = publishedRevisionId; }
    public Long getPreviousPublishedRevisionId() { return previousPublishedRevisionId; }
    public void setPreviousPublishedRevisionId(Long previousPublishedRevisionId) { this.previousPublishedRevisionId = previousPublishedRevisionId; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
