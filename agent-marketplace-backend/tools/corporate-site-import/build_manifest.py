#!/usr/bin/env python3
"""Build a deterministic manifest for an exported corporate-site source tree.

The manifest deliberately records paths relative to the supplied source root.  It
is therefore safe to archive with a release package and can be reproduced after
the same source tree is copied to a different workstation.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import mimetypes
import re
import sys
from collections import Counter
from html.parser import HTMLParser
from pathlib import Path, PurePosixPath
from typing import Dict, Iterable, List, Optional, Tuple
from urllib.parse import unquote, urlsplit


MANIFEST_SCHEMA = "corporate-site-source-manifest/v1"
DISPOSITIONS = ("IMPORT", "ARCHIVE", "APPROVED_EXCLUSION")
MEDIA_SUFFIXES = {".gif", ".jpeg", ".jpg", ".png", ".svg", ".webp"}
TEXT_MIME_TYPES = {
    ".css": "text/css; charset=utf-8",
    ".html": "text/html; charset=utf-8",
    ".htm": "text/html; charset=utf-8",
    ".js": "text/javascript; charset=utf-8",
    ".md": "text/markdown; charset=utf-8",
}
CSS_URL = re.compile(r"url\(\s*(['\"]?)(.*?)\1\s*\)", re.IGNORECASE)


class _LocalReferenceParser(HTMLParser):
    """Extract URL-bearing HTML attributes without treating HTML as content."""

    URL_ATTRIBUTES = {"href", "src", "poster", "data-src"}

    def __init__(self) -> None:
        super().__init__(convert_charrefs=False)
        self.references: List[str] = []

    def handle_starttag(self, tag: str, attrs: List[Tuple[str, Optional[str]]]) -> None:
        for name, value in attrs:
            if name.lower() in self.URL_ATTRIBUTES and value:
                self.references.append(value)


def sha256_file(path: Path) -> str:
    """Return a file SHA-256 without loading binary source assets into memory."""
    digest = hashlib.sha256()
    with path.open("rb") as source:
        for chunk in iter(lambda: source.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def relative_source_path(source_root: Path, path: Path) -> str:
    return path.relative_to(source_root).as_posix()


def legacy_path(relative_path: str) -> str:
    """Return the source-root-relative, URL-shaped legacy path."""
    return "/" + relative_path


def content_type_for(path: Path) -> str:
    suffix = path.suffix.lower()
    if suffix in TEXT_MIME_TYPES:
        return TEXT_MIME_TYPES[suffix]
    if suffix == ".json":
        return "application/json"
    if suffix == ".pdf":
        return "application/pdf"
    if suffix == ".svg":
        return "image/svg+xml"
    guessed, _encoding = mimetypes.guess_type(path.name)
    return guessed or "application/octet-stream"


def classify(relative_path: str, path: Path) -> Tuple[str, str, str]:
    """Return source type, disposition, and a human-reviewable reason.

    Policy is deliberately conservative: files that render the exported site are
    import candidates; local QA evidence and authoring notes remain archived;
    macOS Finder metadata is an approved exclusion.
    """
    suffix = path.suffix.lower()
    path_parts = PurePosixPath(relative_path).parts
    filename = path.name

    if relative_path == "assets/parallel-brand/water-business-framework.png":
        return (
            "superseded_source_media",
            "ARCHIVE",
            "superseded by the approved v2 production-operations framework image",
        )

    if filename == ".DS_Store":
        return (
            "finder_metadata",
            "APPROVED_EXCLUSION",
            "macOS Finder metadata is not website content",
        )
    if path_parts and path_parts[0] == "qa":
        return (
            "quality_assurance_evidence",
            "ARCHIVE",
            "local quality-assurance evidence is retained outside published content",
        )
    if suffix == ".md":
        return (
            "authoring_reference",
            "ARCHIVE",
            "source authoring reference is retained outside published content",
        )
    if path_parts and path_parts[0] == "news-articles" and suffix in {".html", ".htm"}:
        return "news_article", "IMPORT", "legacy news article content"
    if suffix in {".html", ".htm"}:
        return "fixed_page", "IMPORT", "legacy fixed page content"
    if suffix == ".pdf":
        if "软件著作权证书" in path_parts or "专利" in path_parts:
            return "credential_pdf", "IMPORT", "credential or patent PDF"
        return "pdf_document", "IMPORT", "legacy PDF document"
    if suffix in MEDIA_SUFFIXES:
        return "media_asset", "IMPORT", "legacy website media"
    if suffix in {".css", ".js"}:
        return "site_support", "IMPORT", "static resource required by legacy pages"
    if suffix == ".json":
        return "support_file", "IMPORT", "structured static source support file"
    return "support_file", "ARCHIVE", "unmapped source support file retained for review"


def _normalise_reference(reference: str, owner_relative_path: str, known_paths: set[str]) -> Optional[str]:
    """Map a local HTML/CSS reference to a source-relative path when possible."""
    parsed = urlsplit(reference.strip())
    if parsed.scheme or parsed.netloc or reference.strip().startswith(("#", "//")):
        return None
    raw_path = unquote(parsed.path)
    if not raw_path or raw_path.startswith("data:"):
        return None

    if raw_path.startswith("/"):
        candidate = PurePosixPath(raw_path.lstrip("/"))
    else:
        candidate = PurePosixPath(owner_relative_path).parent / PurePosixPath(raw_path)
    normalised = str(candidate)
    if normalised.startswith("../") or normalised == "..":
        return None
    return normalised if normalised in known_paths else None


def _text_references(path: Path, relative_path: str, known_paths: set[str]) -> Iterable[str]:
    """Yield known local targets referenced by an HTML or CSS source file."""
    suffix = path.suffix.lower()
    if suffix not in {".html", ".htm", ".css"}:
        return []
    try:
        text = path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        text = path.read_text(encoding="utf-8", errors="replace")

    raw_references: List[str] = []
    if suffix in {".html", ".htm"}:
        parser = _LocalReferenceParser()
        parser.feed(text)
        parser.close()
        raw_references.extend(parser.references)
    else:
        raw_references.extend(match.group(2) for match in CSS_URL.finditer(text))

    targets = []
    for reference in raw_references:
        target = _normalise_reference(reference, relative_path, known_paths)
        if target is not None:
            targets.append(target)
    return targets


def _source_snapshot_hash(entries: List[Dict[str, object]]) -> str:
    """Hash canonical, source-root-relative file facts only.

    This intentionally omits the machine-local source path, output paths, file
    timestamps, and report-generation time so the same source bytes reproduce
    the same snapshot hash on another machine.
    """
    snapshot_rows = [
        {
            "legacy_path": entry["legacy_path"],
            "source_type": entry["source_type"],
            "disposition": entry["disposition"],
            "content_type": entry["content_type"],
            "size_bytes": entry["size_bytes"],
            "sha256": entry["sha256"],
            "reference_count": entry["reference_count"],
        }
        for entry in entries
    ]
    encoded = json.dumps(
        snapshot_rows,
        ensure_ascii=False,
        separators=(",", ":"),
        sort_keys=True,
    ).encode("utf-8")
    return hashlib.sha256(encoded).hexdigest()


def build_manifest(source_dir: Path) -> Dict[str, object]:
    """Build the complete in-memory manifest for *source_dir*.

    The caller decides where to write it.  No files below source_dir are changed.
    """
    source_root = source_dir.resolve()
    if not source_root.is_dir():
        raise ValueError("source directory does not exist or is not a directory: {}".format(source_dir))

    paths = sorted(
        (path for path in source_root.rglob("*") if path.is_file()),
        key=lambda path: relative_source_path(source_root, path),
    )
    known_paths = {relative_source_path(source_root, path) for path in paths}
    reference_counts: Counter[str] = Counter()
    for path in paths:
        relative_path = relative_source_path(source_root, path)
        reference_counts.update(_text_references(path, relative_path, known_paths))

    entries: List[Dict[str, object]] = []
    for path in paths:
        relative_path = relative_source_path(source_root, path)
        source_type, disposition, reason = classify(relative_path, path)
        entries.append(
            {
                "legacy_path": legacy_path(relative_path),
                "source_type": source_type,
                "disposition": disposition,
                "disposition_reason": reason,
                "content_type": content_type_for(path),
                "size_bytes": path.stat().st_size,
                "sha256": sha256_file(path),
                "reference_count": reference_counts[relative_path],
            }
        )

    disposition_counts = Counter(str(entry["disposition"]) for entry in entries)
    type_counts = Counter(str(entry["source_type"]) for entry in entries)
    summary = {
        "file_count": len(entries),
        "fixed_page_count": type_counts["fixed_page"],
        "news_article_count": type_counts["news_article"],
        "credential_pdf_count": type_counts["credential_pdf"],
        "media_file_count": type_counts["media_asset"],
        "support_file_count": type_counts["site_support"] + type_counts["support_file"],
        "archive_file_count": disposition_counts["ARCHIVE"],
        "approved_exclusion_count": disposition_counts["APPROVED_EXCLUSION"],
        "unclassified_file_count": 0,
        "disposition_counts": {name: disposition_counts[name] for name in DISPOSITIONS},
        "source_type_counts": dict(sorted(type_counts.items())),
    }
    return {
        "schema_version": MANIFEST_SCHEMA,
        "snapshot_sha256": _source_snapshot_hash(entries),
        "classification_policy": {
            "IMPORT": "legacy website pages, certificate PDFs, media, and required static support files",
            "ARCHIVE": "local QA evidence, authoring references, and unmapped support files retained for review",
            "APPROVED_EXCLUSION": "macOS Finder metadata that is not website content",
        },
        "summary": summary,
        "files": entries,
    }


def render_inventory(manifest: Dict[str, object]) -> str:
    """Render a deterministic Markdown inventory from a manifest."""
    summary = manifest["summary"]
    assert isinstance(summary, dict)
    files = manifest["files"]
    assert isinstance(files, list)
    lines = [
        "# 企业官网源内容清单",
        "",
        "本清单由冻结的官网导出源生成。路径均为来源根目录相对的旧站路径；不记录本机绝对目录。",
        "",
        "## 快照",
        "",
        "| 项目 | 数值 |",
        "| --- | --- |",
        "| Manifest 架构 | `{}` |".format(manifest["schema_version"]),
        "| 源快照 SHA-256 | `{}` |".format(manifest["snapshot_sha256"]),
        "| 源文件总数 | {} |".format(summary["file_count"]),
        "| 固定页面 | {} |".format(summary["fixed_page_count"]),
        "| 新闻文章 | {} |".format(summary["news_article_count"]),
        "| 资质 / 专利 PDF | {} |".format(summary["credential_pdf_count"]),
        "| 网站媒体 | {} |".format(summary["media_file_count"]),
        "| 支持文件 | {} |".format(summary["support_file_count"]),
        "| 归档文件 | {} |".format(summary["archive_file_count"]),
        "| 批准排除 | {} |".format(summary["approved_exclusion_count"]),
        "| 未分类文件 | {} |".format(summary["unclassified_file_count"]),
        "",
        "## 分类规则",
        "",
        "- `IMPORT`：迁入页面、新闻、资质 / 专利 PDF、媒体及页面所需静态支持文件。",
        "- `ARCHIVE`：保留本地 QA 证据、作者说明和待人工核定的支持文件，不作为公开内容导入。",
        "- `APPROVED_EXCLUSION`：macOS Finder 元数据，不属于网站内容。",
        "",
        "## 逐项记录",
        "",
        "| 旧站路径 | 类型 | 处理 | MIME | 字节 | SHA-256 | 引用数 |",
        "| --- | --- | --- | --- | ---: | --- | ---: |",
    ]
    for entry in files:
        assert isinstance(entry, dict)
        lines.append(
            "| `{legacy_path}` | {source_type} | {disposition} | `{content_type}` | {size_bytes} | `{sha256}` | {reference_count} |".format(
                **entry
            )
        )
    lines.append("")
    return "\n".join(lines)


def write_outputs(manifest: Dict[str, object], manifest_out: Path, inventory_out: Path) -> None:
    manifest_out.parent.mkdir(parents=True, exist_ok=True)
    inventory_out.parent.mkdir(parents=True, exist_ok=True)
    manifest_out.write_text(
        json.dumps(manifest, ensure_ascii=False, indent=2, sort_keys=False) + "\n",
        encoding="utf-8",
    )
    inventory_out.write_text(render_inventory(manifest), encoding="utf-8")


def parse_args(argv: Optional[List[str]] = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source-dir", required=True, type=Path, help="official-site export directory")
    parser.add_argument("--manifest-out", required=True, type=Path, help="JSON manifest output path")
    parser.add_argument("--inventory-out", required=True, type=Path, help="Markdown inventory output path")
    return parser.parse_args(argv)


def main(argv: Optional[List[str]] = None) -> int:
    args = parse_args(argv)
    try:
        manifest = build_manifest(args.source_dir)
        write_outputs(manifest, args.manifest_out, args.inventory_out)
    except (OSError, ValueError) as error:
        print("manifest build failed: {}".format(error), file=sys.stderr)
        return 2
    summary = manifest["summary"]
    assert isinstance(summary, dict)
    print(
        "manifest created: {file_count} files; {fixed_page_count} fixed pages; "
        "{news_article_count} news articles; {credential_pdf_count} credential PDFs; "
        "snapshot {snapshot}".format(
            snapshot=manifest["snapshot_sha256"],
            **summary
        )
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
