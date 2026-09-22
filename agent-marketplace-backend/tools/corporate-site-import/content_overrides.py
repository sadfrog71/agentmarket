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


def apply_content_overrides(legacy_path: str, title: str, description: str, body_html: str) -> Tuple[str, str, str]:
    """Return publishable content after applying the approved site-only changes."""
    body_html = body_html.replace(_WATER_AI_MENU_ITEM, "")

    if legacy_path == "/water.html":
        title = title.replace("智慧水务 · 衍智云 / 衍云 / 衍数", "智慧水务 · 衍云 / 衍数")
        body_html = body_html.replace(
            "以衍智云、衍云、衍数三条产品线，连接智能应用、业务运行与数据基础。",
            "以衍云、衍数两条产品线，连接业务运行与数据基础。",
        )
        body_html = body_html.replace(_WATER_AI_BRAND_CARD, "")
        body_html, removed = _WATER_AI_SECTION.subn("", body_html, count=1)
        if removed != 1:
            raise ValueError("expected one 衍智云 section in /water.html")
        body_html = body_html.replace('<a href="yanyun.html"><span>02</span>', '<a href="yanyun.html"><span>01</span>')
        body_html = body_html.replace('<a href="yanshu.html"><span>03</span>', '<a href="yanshu.html"><span>02</span>')
        body_html = body_html.replace('<span>02 / 衍云</span>', '<span>01 / 衍云</span>')
        body_html = body_html.replace('<span>03 / 衍数</span>', '<span>02 / 衍数</span>')
        body_html = body_html.replace(_WATER_AI_INDEX_ITEM, "")

    if legacy_path == "/yanyun.html":
        body_html = body_html.replace(SUPERSEDED_FRAMEWORK_ASSET, REPLACEMENT_FRAMEWORK_ASSET)
        body_html = body_html.replace("AI 与数据协同", "数据与应用协同")
        body_html = body_html.replace("衔接衍智云智能能力和衍数数据治理服务。", "衔接智能应用能力和衍数数据治理服务。")
        body_html = body_html.replace(
            '<a class="text-link" href="ai-os.html">衍智云 · 水务 AI OS<span aria-hidden="true">↗</span></a>',
            "",
        )

    return title, description, body_html
