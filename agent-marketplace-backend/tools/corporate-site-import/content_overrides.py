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
_WATER_SUBMENU = re.compile(r'(<div class="submenu" id="sub-water">)(.*?)(</div>)', re.DOTALL)
_YANYUN_MENU_LINK = re.compile(r'(<a\b[^>]*href="yanyun\.html"[^>]*>.*?</a>)', re.DOTALL)
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
_QUALIFICATION_CONTENT = re.compile(
    r'<section class="section wrap secondary-content"><aside>.*?</aside><div>'
    r'<section id="part-1" class="detail-section">.*?</section>'
    r'<section id="part-2" class="detail-section">.*?</section></div></section>',
    re.DOTALL,
)
_QUALIFICATION_MEDIA_PATHS = (
    "qualification-engineering.webp",
    "qualification-smart-water-copyright.webp",
    "qualification-ai-agent-copyright.webp",
    "qualification-wastewater-patent.webp",
    "qualification-fire-hydrant-patent.webp",
    "qualification-nbiot-meter-patent.webp",
)
_QUALIFICATION_CARD_TEMPLATE = (
    '<figure class="qualification-card"><button class="image-expand" type="button" '
    'aria-label="查看资质资料" data-image-title="资质资料">'
    '<img src="assets/parallel-brand/{media_path}" alt="资质资料预览" loading="lazy">'
    '<span class="expand-hint">查看资料 ↗</span></button></figure>'
)
_QUALIFICATION_GALLERY = (
    '<div class="qualification-gallery" aria-label="资质资料展示">'
    + "".join(_QUALIFICATION_CARD_TEMPLATE.format(media_path=path) for path in _QUALIFICATION_MEDIA_PATHS)
    + '</div><p class="source-note">以下为部分资料展示。证书有效状态、权属信息与完整内容以原件及主管部门查询结果为准。</p>'
)
_QUALIFICATION_PRIVATE_CONTENT = (
    '<section class="section wrap secondary-content qualification-privacy"><aside>'
    '<span class="mini-label">本页内容</span><a href="#part-1">资料展示</a></aside><div>'
    '<section id="part-1" class="detail-section"><span class="mini-label">01 / QUALIFICATIONS</span>'
    '<h2>资质资料</h2><p class="detail-lead">仅展示部分归档资料，不对外提供分类汇总。</p>'
    + _QUALIFICATION_GALLERY
    + '</section></div></section>'
)
_QUALIFICATION_IMAGE_DIALOG = (
    '<dialog id="image-viewer" class="image-viewer"><button class="dialog-close" aria-label="关闭大图">×</button>'
    '<h2 id="image-viewer-title"></h2><img id="image-viewer-img" alt=""><p id="image-viewer-caption"></p>'
    '<a id="image-original" class="text-link" target="_blank" rel="noopener noreferrer">打开原图查看细节 ↗</a></dialog>'
)


def _ensure_drainage_menu_item(body_html: str) -> str:
    def ensure_item(match: re.Match[str]) -> str:
        menu_items = match.group(2)
        if re.search(r'<a\b[^>]*href="drainage\.html"', menu_items):
            return match.group(0)
        menu_items = _YANYUN_MENU_LINK.sub(lambda yanyun: yanyun.group(1) + _DRAINAGE_MENU_ITEM, menu_items, count=1)
        return match.group(1) + menu_items + match.group(3)

    return _WATER_SUBMENU.sub(ensure_item, body_html, count=1)


def apply_content_overrides(legacy_path: str, title: str, description: str, body_html: str) -> Tuple[str, str, str]:
    """Return publishable content after applying the approved site-only changes."""
    body_html = body_html.replace(_WATER_AI_MENU_ITEM, "")
    body_html = _ensure_drainage_menu_item(body_html)

    if legacy_path == "/water.html":
        title = title.replace("智慧水务 · 衍智云 / 衍云 / 衍数", "智慧水务 · 衍云 / 智慧排水 / 衍数")
        body_html = body_html.replace(
            "以衍智云、衍云、衍数三条产品线，连接智能应用、业务运行与数据基础。",
            "以衍云、智慧排水、衍数三条产品线，连接业务运行、排水调度与数据基础。",
        )
        body_html = body_html.replace(_WATER_AI_BRAND_CARD, "")
        body_html = _WATER_AI_SECTION.sub("", body_html, count=1)
        body_html = body_html.replace('<a href="yanyun.html"><span>02</span>', '<a href="yanyun.html"><span>01</span>')
        body_html = body_html.replace('<span>02 / 衍云</span>', '<span>01 / 衍云</span>')
        if _DRAINAGE_BRAND_CARD not in body_html:
            body_html = body_html.replace(_YANYUN_BRAND_CARD, _YANYUN_BRAND_CARD + _DRAINAGE_BRAND_CARD)
        if 'id="drainage"' not in body_html:
            body_html = body_html.replace(
                '<section class="section wrap" id="yanshu">',
                _DRAINAGE_SECTION + '<section class="section wrap" id="yanshu">',
            )
        body_html = body_html.replace(_WATER_AI_INDEX_ITEM, "")
        if _DRAINAGE_INDEX_ITEM not in body_html:
            body_html = body_html.replace(_YANYUN_INDEX_ITEM, _YANYUN_INDEX_ITEM + _DRAINAGE_INDEX_ITEM)

    if legacy_path == "/yanyun.html":
        body_html = body_html.replace(SUPERSEDED_FRAMEWORK_ASSET, REPLACEMENT_FRAMEWORK_ASSET)
        body_html = body_html.replace("AI 与数据协同", "数据与应用协同")
        body_html = body_html.replace("衔接衍智云智能能力和衍数数据治理服务。", "衔接智能应用能力和衍数数据治理服务。")
        body_html = body_html.replace(
            '<a class="text-link" href="ai-os.html">衍智云 · 水务 AI OS<span aria-hidden="true">↗</span></a>',
            "",
        )

    if legacy_path == "/qualifications.html":
        if "qualification-privacy" not in body_html:
            body_html, replacement_count = _QUALIFICATION_CONTENT.subn(
                _QUALIFICATION_PRIVATE_CONTENT, body_html, count=1
            )
            if replacement_count != 1:
                raise ValueError("expected qualification content in /qualifications.html")
        if 'id="image-viewer"' not in body_html:
            body_html = body_html.replace("</main>", _QUALIFICATION_IMAGE_DIALOG + "</main>", 1)

    return title, description, body_html
