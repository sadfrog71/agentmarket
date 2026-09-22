package com.ruoyi.site.domain;

/**
 * 后台保存页面草稿的受限命令对象。页面布局由 templateCode 固定，正文不接收脚本、样式或组件定义。
 */
public class SitePageDraft extends SitePageRevision
{
    private static final long serialVersionUID = 1L;

    private Long pageId;
    private String pageCode;
    private String routePath;
    private String templateCode;
    private Long draftRevisionId;
    private Long publishedRevisionId;

    public Long getPageId() { return pageId; }
    public void setPageId(Long pageId) { this.pageId = pageId; }
    public String getPageCode() { return pageCode; }
    public void setPageCode(String pageCode) { this.pageCode = pageCode; }
    public String getRoutePath() { return routePath; }
    public void setRoutePath(String routePath) { this.routePath = routePath; }
    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public Long getDraftRevisionId() { return draftRevisionId; }
    public void setDraftRevisionId(Long draftRevisionId) { this.draftRevisionId = draftRevisionId; }
    public Long getPublishedRevisionId() { return publishedRevisionId; }
    public void setPublishedRevisionId(Long publishedRevisionId) { this.publishedRevisionId = publishedRevisionId; }
}
