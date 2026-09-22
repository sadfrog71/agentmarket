"""Approved presentation overrides applied while importing the corporate site.

The exported website remains a traceable reference.  These narrowly-scoped
overrides express later, approved editorial and visual changes in the
published-content package, rather than silently rewriting the source export.
"""

from __future__ import annotations

import re
from typing import Tuple


SUPERSEDED_FRAMEWORK_ASSET = "assets/parallel-brand/water-business-framework.png"
REPLACEMENT_FRAMEWORK_ASSET = "assets/parallel-brand/water-business-framework-v2.png"

_WATER_AI_MENU_ITEM = '<a href="ai-os.html">衍智云 · 水务 AI OS<span>↗</span></a>'
_WATER_AI_INDEX_ITEM = '<a class="text-link" href="ai-os.html">衍智云 · 水务 AI OS<span aria-hidden="true">↗</span></a>'
_WATER_AI_BRAND_CARD = '<a href="ai-os.html"><span>01</span><h2>衍智云</h2><p>水务 AI OS</p><b>↓</b></a>'
_WATER_AI_SECTION = re.compile(r'<section class="section wrap" id="yanzhiyun">.*?</section>', re.DOTALL)
_YANYUN_MENU_ITEM = '<a href="yanyun.html">衍云 · 一体化水务平台<span>↗</span></a>'
_DRAINAGE_MENU_ITEM = '<a href="drainage.html">智慧排水 · 厂站网河调度<span>↗</span></a>'
_YANYUN_BRAND_CARD = '<a href="yanyun.html"><span>01</span><h2>衍云</h2><p>一体化水务平台</p><b>↓</b></a>'
_DRAINAGE_BRAND_CARD = '<a href="drainage.html"><span>02</span><h2>智慧排水</h2><p>厂站网河一体化调度</p><b>↓</b></a>'
_YANYUN_INDEX_ITEM = '<a class="text-link" href="yanyun.html">衍云 · 一体化水务平台<span aria-hidden="true">↗</span></a>'
_DRAINAGE_INDEX_ITEM = '<a class="text-link" href="drainage.html">智慧排水 · 厂站网河调度<span aria-hidden="true">↗</span></a>'
_DRAINAGE_SECTION = (
    '<section class="section drainage-entry" id="drainage"><div class="wrap">'
    '<div class="section-heading"><div><div class="eyebrow"><span>02 / 智慧排水</span>PARALLEL DIGITAL</div>'
    '<h2>厂站网河联动，<br>让排水调度更主动。</h2></div><div class="section-intro">'
    '<p>连接污水厂、泵站、管网与河道，将实时感知、拓扑关系、联动调度和事件处置组织在同一业务链路中。</p>'
    '<a class="text-link" href="drainage.html">了解智慧排水<span aria-hidden="true">↗</span></a></div></div>'
    '<div class="drainage-teaser"><div><span class="mini-label">FACTORY · STATION · NETWORK · RIVER</span>'
    '<h3>从单点监控<br>走向全链条协同。</h3><p>围绕看得见、管得住、调得动、算得准，构建面向运行人员的排水调度视图。</p>'
    '<div class="drainage-tags"><span>泵站拓扑</span><span>GIS 一张图</span><span>联动调度</span><span>报警工单</span></div></div>'
    '<figure><img src="assets/parallel-brand/smart-drainage-topology.webp" alt="泵站拓扑结构与联动调度示意" loading="lazy">'
    '<figcaption>泵站拓扑结构与联动调度</figcaption></figure></div>'
    '<p class="source-note">图示用于说明产品思路；实际接入对象、数据范围、控制权限与交付阶段按项目现状确定。</p>'
    '</div></section>'
)
_QUALIFICATION_SECTION_MARKER = '</div></section><section id="part-2" class="detail-section">'
_QUALIFICATION_GALLERY = (
    '<div class="qualification-gallery" aria-label="代表性资质与知识产权">'
    '<figure class="qualification-card"><button class="image-expand" type="button" '
    'aria-label="放大查看：电子与智能化工程专业承包二级资质" data-image-title="工程与服务资质">'
    '<img src="assets/parallel-brand/qualification-engineering.webp" alt="电子与智能化工程专业承包二级资质证书" loading="lazy">'
    '<span class="expand-hint">查看大图 ↗</span></button><figcaption><span>工程与服务</span>'
    '<strong>电子与智能化工程专业承包二级</strong></figcaption></figure>'
    '<figure class="qualification-card"><button class="image-expand" type="button" '
    'aria-label="放大查看：智慧水务平台软件著作权" data-image-title="代表性软件著作权">'
    '<img src="assets/parallel-brand/qualification-smart-water-copyright.webp" alt="平行数字智慧水务平台软件著作权证明" loading="lazy">'
    '<span class="expand-hint">查看大图 ↗</span></button><figcaption><span>软件成果</span>'
    '<strong>智慧水务平台软件著作权</strong></figcaption></figure>'
    '<figure class="qualification-card"><button class="image-expand" type="button" '
    'aria-label="放大查看：污水处理运行控制系统发明专利" data-image-title="代表性发明专利">'
    '<img src="assets/parallel-brand/qualification-wastewater-patent.webp" alt="一种污水处理运行控制系统发明专利证书" loading="lazy">'
    '<span class="expand-hint">查看大图 ↗</span></button><figcaption><span>技术成果</span>'
    '<strong>污水处理运行控制系统发明专利</strong></figcaption></figure>'
    '</div><p class="source-note">以下为当前归档中的代表性资料。证书有效状态、权属信息与完整内容以原件及主管部门查询结果为准。</p>'
)
_QUALIFICATION_IMAGE_DIALOG = (
    '<dialog id="image-viewer" class="image-viewer"><button class="dialog-close" aria-label="关闭大图">×</button>'
    '<h2 id="image-viewer-title"></h2><img id="image-viewer-img" alt=""><p id="image-viewer-caption"></p>'
    '<a id="image-original" class="text-link" target="_blank" rel="noopener noreferrer">打开原图查看细节 ↗</a></dialog>'
)


def apply_content_overrides(legacy_path: str, title: str, description: str, body_html: str) -> Tuple[str, str, str]:
    """Return publishable content after applying the approved site-only changes."""
    body_html = body_html.replace(_WATER_AI_MENU_ITEM, "")
    if 'href="drainage.html"' not in body_html:
        body_html = body_html.replace(_YANYUN_MENU_ITEM, _YANYUN_MENU_ITEM + _DRAINAGE_MENU_ITEM)

    if legacy_path == "/water.html":
        title = title.replace("智慧水务 · 衍智云 / 衍云 / 衍数", "智慧水务 · 衍云 / 智慧排水 / 衍数")
        body_html = body_html.replace(
            "以衍智云、衍云、衍数三条产品线，连接智能应用、业务运行与数据基础。",
            "以衍云、智慧排水、衍数三条产品线，连接业务运行、排水调度与数据基础。",
        )
        body_html = body_html.replace(_WATER_AI_BRAND_CARD, "")
        body_html, removed = _WATER_AI_SECTION.subn("", body_html, count=1)
        if removed != 1:
            raise ValueError("expected one 衍智云 section in /water.html")
        body_html = body_html.replace('<a href="yanyun.html"><span>02</span>', '<a href="yanyun.html"><span>01</span>')
        body_html = body_html.replace('<span>02 / 衍云</span>', '<span>01 / 衍云</span>')
        body_html = body_html.replace(_YANYUN_BRAND_CARD, _YANYUN_BRAND_CARD + _DRAINAGE_BRAND_CARD)
        body_html = body_html.replace('<section class="section wrap" id="yanshu">', _DRAINAGE_SECTION + '<section class="section wrap" id="yanshu">')
        body_html = body_html.replace(_WATER_AI_INDEX_ITEM, "")
        body_html = body_html.replace(_YANYUN_INDEX_ITEM, _YANYUN_INDEX_ITEM + _DRAINAGE_INDEX_ITEM)

    if legacy_path == "/yanyun.html":
        body_html = body_html.replace(SUPERSEDED_FRAMEWORK_ASSET, REPLACEMENT_FRAMEWORK_ASSET)
        body_html = body_html.replace("AI 与数据协同", "数据与应用协同")
        body_html = body_html.replace("衔接衍智云智能能力和衍数数据治理服务。", "衔接智能应用能力和衍数数据治理服务。")
        body_html = body_html.replace(
            '<a class="text-link" href="ai-os.html">衍智云 · 水务 AI OS<span aria-hidden="true">↗</span></a>',
            "",
        )

    if legacy_path == "/qualifications.html" and 'class="qualification-gallery"' not in body_html:
        if _QUALIFICATION_SECTION_MARKER not in body_html:
            raise ValueError("expected qualification section marker in /qualifications.html")
        body_html = body_html.replace(
            "国家高新技术企业、双软企业、CMMI 三级。",
            "国家高新技术企业、双软企业；CMMI 三级为历史认证记录。",
        )
        body_html = body_html.replace(
            _QUALIFICATION_SECTION_MARKER,
            '</div>' + _QUALIFICATION_GALLERY + '</section><section id="part-2" class="detail-section">',
            1,
        )
        body_html = body_html.replace("</main>", _QUALIFICATION_IMAGE_DIALOG + "</main>", 1)

    return title, description, body_html
