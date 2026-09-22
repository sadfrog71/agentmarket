#!/usr/bin/env python3
"""Generate a repeatable, source-traceable corporate-site import SQL package.

The script reads a frozen source tree plus its U7 manifest.  It does not open a
database or mutate the source tree.  The generated SQL uses only additive
inserts and source hashes; re-running the same package skips already imported
objects rather than overwriting an editor's later draft.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import posixpath
import re
import uuid
from datetime import datetime, timezone
from html import unescape
from pathlib import Path, PurePosixPath
from typing import Dict, Iterable, List, Optional, Sequence, Tuple
from urllib.parse import unquote, urlsplit

from content_overrides import apply_content_overrides


NAMESPACE = uuid.UUID("f87f0be6-53ce-4fa9-87ca-f7d5ac7fc996")
BODY_RE = re.compile(r"<body[^>]*>(?P<body>.*)</body\s*>", re.IGNORECASE | re.DOTALL)
MAIN_RE = re.compile(r"<main[^>]*>(?P<main>.*)</main\s*>", re.IGNORECASE | re.DOTALL)
TITLE_RE = re.compile(r"<title>(?P<title>.*?)</title\s*>", re.IGNORECASE | re.DOTALL)
DESCRIPTION_RE = re.compile(
    r"<meta\s+name=[\"']description[\"']\s+content=[\"'](?P<description>.*?)[\"']\s*/?>",
    re.IGNORECASE | re.DOTALL,
)
SCRIPT_RE = re.compile(r"<script\b[^>]*>.*?</script\s*>", re.IGNORECASE | re.DOTALL)
URL_ATTRIBUTE_RE = re.compile(r"(?P<name>src|href)=(?P<quote>[\"'])(?P<value>.*?)(?P=quote)", re.IGNORECASE)
CERTIFICATE_CALL_RE = re.compile(r"initCertificates\((?P<data>\[.*?\])\);", re.DOTALL)
NEWS_RE = re.compile(
    r"\{cat:'(?P<category>[^']+)',date:(?P<date>null|'[^']*'),title:'(?P<title>(?:\\'|[^'])*)'.*?art:'(?P<path>[^']+)'\}",
    re.DOTALL,
)


def sha256_bytes(value: bytes) -> str:
    return hashlib.sha256(value).hexdigest()


def sql_literal(value: Optional[str]) -> str:
    if value is None:
        return "null"
    return "'" + value.replace("\\", "\\\\").replace("'", "\\'").replace("\x00", "") + "'"


def sql_utf8(value: Optional[str]) -> str:
    if value is None:
        return "null"
    if value == "":
        return "''"
    return "convert(0x{} using utf8mb4)".format(value.encode("utf-8").hex())


def stable_uuid(kind: str, legacy_path: str) -> str:
    return str(uuid.uuid5(NAMESPACE, "{}:{}".format(kind, legacy_path)))


def file_key(entry: Dict[str, object]) -> str:
    return str(entry["legacy_path"])


def load_manifest(path: Path) -> Dict[str, object]:
    manifest = json.loads(path.read_text(encoding="utf-8"))
    if manifest.get("schema_version") != "corporate-site-source-manifest/v1":
        raise ValueError("unsupported manifest schema")
    if int(manifest.get("summary", {}).get("unclassified_file_count", -1)) != 0:
        raise ValueError("manifest contains unclassified source files")
    return manifest


def public_media_entries(manifest: Dict[str, object]) -> Dict[str, Dict[str, object]]:
    result: Dict[str, Dict[str, object]] = {}
    for entry in manifest["files"]:
        if entry["disposition"] != "IMPORT":
            continue
        if entry["source_type"] not in {"media_asset", "credential_pdf", "pdf_document"}:
            continue
        result[file_key(entry)] = entry
    return result


def source_relative_url(owner_path: str, value: str) -> Optional[str]:
    parsed = urlsplit(value.strip())
    if parsed.scheme or parsed.netloc or value.startswith(("#", "//", "data:")):
        return None
    candidate = unquote(parsed.path)
    if not candidate:
        return None
    if candidate.startswith("/"):
        path = candidate.lstrip("/")
    else:
        path = str(PurePosixPath(owner_path.lstrip("/")).parent / PurePosixPath(candidate))
    normalised = posixpath.normpath(path).lstrip("/")
    if normalised in {".", ".."} or normalised.startswith("../"):
        return None
    return "/" + normalised


def rewrite_media_urls(html: str, owner_path: str, media: Dict[str, Dict[str, object]]) -> Tuple[str, List[str]]:
    references: List[str] = []

    def replace(match: re.Match[str]) -> str:
        source_key = source_relative_url(owner_path, match.group("value"))
        if source_key is None or source_key not in media:
            return match.group(0)
        references.append(source_key)
        public_id = stable_uuid("media", source_key)
        return '{}="/api/open/site/v1/media/{}"'.format(match.group("name"), public_id)

    return URL_ATTRIBUTE_RE.sub(replace, html), sorted(set(references))


def extract_html(path: Path, owner_path: str, media: Dict[str, Dict[str, object]]) -> Tuple[str, str, str, List[str], Dict[str, object]]:
    text = path.read_text(encoding="utf-8", errors="replace")
    title_match = TITLE_RE.search(text)
    description_match = DESCRIPTION_RE.search(text)
    body_match = BODY_RE.search(text)
    if body_match is None:
        raise ValueError("missing body tag: {}".format(path))
    body = body_match.group("body")
    extra: Dict[str, object] = {}
    certificates_match = CERTIFICATE_CALL_RE.search(body)
    if certificates_match:
        try:
            certificate_items = json.loads(certificates_match.group("data"))
            for item in certificate_items:
                source_key = source_relative_url(owner_path, str(item.get("url", "")))
                if source_key in media:
                    item["url"] = "/api/open/site/v1/media/{}".format(stable_uuid("media", source_key))
            extra["certificates"] = certificate_items
        except json.JSONDecodeError as error:
            raise ValueError("invalid certificate payload in {}: {}".format(path, error)) from error
    body = SCRIPT_RE.sub("", body)
    title = unescape(re.sub(r"<[^>]+>", "", title_match.group("title")).strip()) if title_match else path.stem
    description = unescape(description_match.group("description")).strip() if description_match else ""
    title, description, body = apply_content_overrides(owner_path, title, description, body)
    rewritten, references = rewrite_media_urls(body, owner_path, media)
    return title, description, rewritten.strip(), references, extra


def page_code(legacy_path: str) -> str:
    name = PurePosixPath(legacy_path).stem
    if name == "index":
        return "HOME"
    return re.sub(r"[^A-Za-z0-9]+", "_", name).strip("_").upper()


def parse_news(source_root: Path) -> List[Dict[str, Optional[str]]]:
    text = (source_root / "assets" / "news-data.js").read_text(encoding="utf-8")
    news: List[Dict[str, Optional[str]]] = []
    for match in NEWS_RE.finditer(text):
        raw_date = match.group("date")
        news.append(
            {
                "category": match.group("category"),
                "date": None if raw_date == "null" else raw_date.strip("'"),
                "title": match.group("title").replace("\\'", "'"),
                "legacy_path": "/" + match.group("path"),
            }
        )
    if len(news) != 53:
        raise ValueError("expected 53 news rows in news-data.js, found {}".format(len(news)))
    return news


def insert_media_sql(media: Dict[str, Dict[str, object]], import_run_id: str) -> Iterable[str]:
    for legacy_path, entry in sorted(media.items()):
        extension = PurePosixPath(legacy_path).suffix.lower()
        public_id = stable_uuid("media", legacy_path)
        storage_key = "available/{}{}".format(public_id, extension)
        kind = "DOCUMENT" if extension == ".pdf" else "IMAGE"
        yield (
            "insert into site_media (public_id, media_kind, original_filename, source_legacy_key, source_sha256, "
            "content_sha256, mime_type, file_size, media_state, storage_key, public_path, import_run_id, adopted_at, create_by, remark) "
            "select {public_id}, {kind}, {filename}, {source_key}, {hash_value}, {hash_value}, {mime_type}, {size}, "
            "'AVAILABLE', {storage_key}, {public_path}, {run_id}, sysdate(), 'site-import', '冻结源快照导入' "
            "where not exists (select 1 from site_media where source_legacy_key = {source_key});"
        ).format(
            public_id=sql_literal(public_id), kind=sql_literal(kind), filename=sql_utf8(PurePosixPath(legacy_path).name),
            source_key=sql_utf8(legacy_path), hash_value=sql_literal(str(entry["sha256"])), mime_type=sql_literal(str(entry["content_type"]).split(";", 1)[0]),
            size=int(entry["size_bytes"]), storage_key=sql_literal(storage_key), public_path=sql_literal("/open/site/v1/media/{}".format(public_id)),
            run_id=sql_literal(import_run_id),
        )


def insert_page_sql(source_root: Path, entry: Dict[str, object], media: Dict[str, Dict[str, object]], import_run_id: str) -> Iterable[str]:
    legacy_path = file_key(entry)
    title, description, body_html, references, extra = extract_html(source_root / legacy_path.lstrip("/"), legacy_path, media)
    category_paths = {
        "/news-corp.html": "corp",
        "/news-tech.html": "tech",
        "/news-ind.html": "ind",
    }
    if legacy_path in category_paths:
        extra["newsCategory"] = category_paths[legacy_path]
    code = page_code(legacy_path)
    content_hash = sha256_bytes((title + "\n" + description + "\n" + body_html).encode("utf-8"))
    seo_json = json.dumps({"description": description}, ensure_ascii=False, separators=(",", ":"))
    data_json = json.dumps(extra, ensure_ascii=False, separators=(",", ":")) if extra else None
    yield (
        "insert into site_page (page_code, legacy_key, route_path, template_code, create_by, remark) "
        "select {code}, {legacy_key}, {route}, 'LEGACY_PAGE', 'site-import', '由冻结官网源导入' "
        "where not exists (select 1 from site_page where page_code = {code});"
    ).format(code=sql_literal(code), legacy_key=sql_utf8(legacy_path), route=sql_utf8(legacy_path))
    yield "select page_id into @site_page_id from site_page where page_code = {};".format(sql_literal(code))
    yield (
        "insert into site_page_revision (page_id, revision_no, revision_state, title, subtitle, body_html, seo_json, "
        "page_data_json, content_hash, source_import_run_id, published_at, published_by, create_by, remark) "
        "select @site_page_id, 1, 'PUBLISHED', {title}, {subtitle}, {body_html}, {seo_json}, {data_json}, {content_hash}, "
        "{run_id}, sysdate(), 'site-import', 'site-import', '冻结官网源导入' "
        "where not exists (select 1 from site_page_revision where page_id = @site_page_id and revision_no = 1);"
    ).format(title=sql_utf8(title), subtitle=sql_utf8(description), body_html=sql_utf8(body_html), seo_json=sql_utf8(seo_json),
             data_json=sql_utf8(data_json), content_hash=sql_literal(content_hash), run_id=sql_literal(import_run_id))
    yield "select revision_id into @site_page_revision_id from site_page_revision where page_id = @site_page_id and revision_no = 1;"
    yield (
        "update site_page set published_revision_id = @site_page_revision_id, draft_revision_id = null, "
        "update_by = 'site-import', update_time = sysdate() where page_id = @site_page_id and published_revision_id is null;"
    )
    yield (
        "insert ignore into site_publish_audit (aggregate_type, aggregate_id, action_type, from_revision_id, to_revision_id, operator, reason) "
        "values ('PAGE', @site_page_id, 'PUBLISH', null, @site_page_revision_id, 'site-import', '冻结官网源导入');"
    )
    for source_key in references:
        yield (
            "insert ignore into site_media_reference (media_id, owner_type, owner_id, reference_role, field_key, sort_no) "
            "select media_id, 'PAGE_REVISION', @site_page_revision_id, 'BODY', {field_key}, 0 from site_media "
            "where source_legacy_key = {source_key};"
        ).format(field_key=sql_utf8(source_key), source_key=sql_utf8(source_key))


def insert_article_sql(source_root: Path, row: Dict[str, Optional[str]], media: Dict[str, Dict[str, object]], import_run_id: str) -> Iterable[str]:
    legacy_path = str(row["legacy_path"])
    title, description, body_html, references, _extra = extract_html(source_root / legacy_path.lstrip("/"), legacy_path, media)
    expected_title = str(row["title"])
    if expected_title != title:
        title = expected_title
    article_code = page_code(legacy_path).replace("A", "ARTICLE_", 1)
    content_hash = sha256_bytes((title + "\n" + description + "\n" + body_html).encode("utf-8"))
    seo_json = json.dumps({"description": description}, ensure_ascii=False, separators=(",", ":"))
    yield (
        "insert into site_article (article_code, legacy_key, legacy_path, category_id, create_by, remark) "
        "select {code}, {legacy_key}, {legacy_path}, (select category_id from site_article_category where category_code = {category}), "
        "'site-import', '冻结官网源导入' where not exists (select 1 from site_article where article_code = {code});"
    ).format(code=sql_literal(article_code), legacy_key=sql_utf8(legacy_path), legacy_path=sql_utf8(legacy_path), category=sql_literal(str(row["category"])))
    yield "select article_id into @site_article_id from site_article where article_code = {};".format(sql_literal(article_code))
    yield (
        "insert into site_article_revision (article_id, revision_no, revision_state, title, summary, body_html, body_text, seo_json, "
        "content_hash, source_import_run_id, published_at, published_by, create_by, remark) "
        "select @site_article_id, 1, 'PUBLISHED', {title}, {summary}, {body_html}, {body_text}, {seo_json}, {content_hash}, {run_id}, "
        "{published_at}, 'site-import', 'site-import', '冻结官网源导入' "
        "where not exists (select 1 from site_article_revision where article_id = @site_article_id and revision_no = 1);"
    ).format(title=sql_utf8(title), summary=sql_utf8(description), body_html=sql_utf8(body_html),
             body_text=sql_utf8(re.sub(r"<[^>]+>", " ", body_html)), seo_json=sql_utf8(seo_json), content_hash=sql_literal(content_hash),
             run_id=sql_literal(import_run_id), published_at=sql_literal(row["date"]) if row["date"] else "null")
    yield "select revision_id into @site_article_revision_id from site_article_revision where article_id = @site_article_id and revision_no = 1;"
    yield (
        "update site_article set published_revision_id = @site_article_revision_id, draft_revision_id = null, "
        "update_by = 'site-import', update_time = sysdate() where article_id = @site_article_id and published_revision_id is null;"
    )
    yield (
        "insert ignore into site_publish_audit (aggregate_type, aggregate_id, action_type, from_revision_id, to_revision_id, operator, reason) "
        "values ('ARTICLE', @site_article_id, 'PUBLISH', null, @site_article_revision_id, 'site-import', '冻结官网源导入');"
    )
    for source_key in references:
        yield (
            "insert ignore into site_media_reference (media_id, owner_type, owner_id, reference_role, field_key, sort_no) "
            "select media_id, 'ARTICLE_REVISION', @site_article_revision_id, 'BODY', {field_key}, 0 from site_media "
            "where source_legacy_key = {source_key};"
        ).format(field_key=sql_utf8(source_key), source_key=sql_utf8(source_key))


def insert_credentials_sql(manifest: Dict[str, object], import_run_id: str) -> Iterable[str]:
    for entry in manifest["files"]:
        if entry["source_type"] != "credential_pdf" or entry["disposition"] != "IMPORT":
            continue
        legacy_path = file_key(entry)
        kind = "SOFTWARE_COPYRIGHT" if legacy_path.startswith("/软件著作权证书/") else "PATENT"
        code = "CREDENTIAL_" + stable_uuid("credential", legacy_path).replace("-", "").upper()
        title = PurePosixPath(legacy_path).stem
        yield (
            "insert into site_credential (credential_code, legacy_key, credential_type, create_by, remark) "
            "select {code}, {legacy_key}, {kind}, 'site-import', '冻结官网源导入' "
            "where not exists (select 1 from site_credential where credential_code = {code});"
        ).format(code=sql_literal(code), legacy_key=sql_utf8(legacy_path), kind=sql_literal(kind))
        yield "select credential_id into @site_credential_id from site_credential where credential_code = {};".format(sql_literal(code))
        yield (
            "insert into site_credential_revision (credential_id, revision_no, revision_state, title, document_media_id, content_hash, "
            "source_import_run_id, published_at, published_by, create_by, remark) "
            "select @site_credential_id, 1, 'PUBLISHED', {title}, (select media_id from site_media where source_legacy_key = {legacy_key}), "
            "{hash_value}, {run_id}, sysdate(), 'site-import', 'site-import', '冻结官网源导入' "
            "where not exists (select 1 from site_credential_revision where credential_id = @site_credential_id and revision_no = 1);"
        ).format(title=sql_utf8(title), legacy_key=sql_utf8(legacy_path), hash_value=sql_literal(str(entry["sha256"])), run_id=sql_literal(import_run_id))
        yield "select revision_id into @site_credential_revision_id from site_credential_revision where credential_id = @site_credential_id and revision_no = 1;"
        yield (
            "update site_credential set published_revision_id = @site_credential_revision_id, draft_revision_id = null, "
            "update_by = 'site-import', update_time = sysdate() where credential_id = @site_credential_id and published_revision_id is null;"
        )
        yield (
            "insert ignore into site_media_reference (media_id, owner_type, owner_id, reference_role, field_key, sort_no) "
            "select media_id, 'CREDENTIAL_REVISION', @site_credential_revision_id, 'ATTACHMENT', {field_key}, 0 from site_media "
            "where source_legacy_key = {legacy_key};"
        ).format(field_key=sql_utf8(legacy_path), legacy_key=sql_utf8(legacy_path))


def insert_import_ledger_sql(manifest: Dict[str, object], import_run_id: str) -> Iterable[str]:
    manifest_hash = sha256_bytes(json.dumps(manifest, ensure_ascii=False, sort_keys=True, separators=(",", ":")).encode("utf-8"))
    files = manifest["files"]
    yield (
        "insert into site_import_run (import_run_id, manifest_id, source_snapshot_hash, manifest_sha256, run_status, started_by, "
        "started_at, completed_at, total_items, imported_items, excluded_items, report_json, remark) values "
        "({run_id}, {manifest_id}, {snapshot_hash}, {manifest_hash}, 'COMPLETED', 'site-import', sysdate(), sysdate(), {total}, {imported}, "
        "{excluded}, {report}, '冻结官网源导入') on duplicate key update completed_at = completed_at;"
    ).format(run_id=sql_literal(import_run_id), manifest_id=sql_literal("official-site-" + str(manifest["snapshot_sha256"])[:12]),
             snapshot_hash=sql_literal(str(manifest["snapshot_sha256"])), manifest_hash=sql_literal(manifest_hash), total=len(files),
             imported=sum(1 for entry in files if entry["disposition"] == "IMPORT"),
             excluded=sum(1 for entry in files if entry["disposition"] != "IMPORT"),
             report=sql_utf8(json.dumps(manifest["summary"], ensure_ascii=False, separators=(",", ":"))))
    for entry in files:
        disposition = str(entry["disposition"])
        state = "IMPORTED" if disposition == "IMPORT" else "EXCLUDED"
        target_type = {"fixed_page": "PAGE", "news_article": "ARTICLE", "credential_pdf": "CREDENTIAL", "media_asset": "MEDIA", "site_support": "TEMPLATE_ASSET"}.get(str(entry["source_type"]))
        yield (
            "insert ignore into site_import_item (import_run_id, manifest_item_id, source_key, source_relative_path, source_sha256, "
            "classification, target_type, item_state, processed_at, evidence_json) values "
            "({run_id}, {item_id}, {source_key}, {relative_path}, {hash_value}, {classification}, {target_type}, {state}, sysdate(), {evidence});"
        ).format(run_id=sql_literal(import_run_id), item_id=sql_literal(stable_uuid("manifest-item", file_key(entry))),
                 source_key=sql_utf8(file_key(entry)), relative_path=sql_utf8(file_key(entry).lstrip("/")), hash_value=sql_literal(str(entry["sha256"])),
                 classification=sql_literal(disposition), target_type=sql_literal(target_type), state=sql_literal(state),
                 evidence=sql_utf8(json.dumps({"source_type": entry["source_type"], "size_bytes": entry["size_bytes"], "reference_count": entry["reference_count"]}, separators=(",", ":"))))


def media_copy_plan(media: Dict[str, Dict[str, object]]) -> Dict[str, object]:
    items = []
    for legacy_path, entry in sorted(media.items()):
        public_id = stable_uuid("media", legacy_path)
        items.append({
            "source_relative_path": legacy_path.lstrip("/"),
            "target_storage_key": "available/{}{}".format(public_id, PurePosixPath(legacy_path).suffix.lower()),
            "public_id": public_id,
            "sha256": entry["sha256"],
            "size_bytes": entry["size_bytes"],
            "mime_type": str(entry["content_type"]).split(";", 1)[0],
        })
    return {"schema_version": "corporate-site-media-copy-plan/v1", "items": items}


def generate(source_dir: Path, manifest: Dict[str, object]) -> Tuple[str, Dict[str, object]]:
    source_root = source_dir.resolve()
    media = public_media_entries(manifest)
    import_run_id = stable_uuid("import-run", str(manifest["snapshot_sha256"]))
    lines = [
        "-- Generated from a frozen source manifest. Do not edit this file by hand.",
        "-- No destructive statement is emitted. Run only after migration 006 is recorded.",
        "set names utf8mb4;",
        "start transaction;",
    ]
    lines.extend(insert_import_ledger_sql(manifest, import_run_id))
    lines.extend(insert_media_sql(media, import_run_id))
    lines.extend([
        "insert into site_article_category (category_code, category_name, route_slug, sort_no, create_by, remark) values "
        "('corp', '企业新闻', 'corp', 1, 'site-import', '冻结官网源导入'), "
        "('tech', '技术科普', 'tech', 2, 'site-import', '冻结官网源导入'), "
        "('ind', '行业资讯', 'ind', 3, 'site-import', '冻结官网源导入') "
        "on duplicate key update category_name = values(category_name);"
    ])
    for entry in manifest["files"]:
        if entry["source_type"] == "fixed_page" and entry["disposition"] == "IMPORT":
            lines.extend(insert_page_sql(source_root, entry, media, import_run_id))
    for row in parse_news(source_root):
        lines.extend(insert_article_sql(source_root, row, media, import_run_id))
    lines.extend(insert_credentials_sql(manifest, import_run_id))
    lines.append("commit;")
    return "\n".join(lines) + "\n", media_copy_plan(media)


def parse_args(argv: Optional[Sequence[str]] = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source-dir", required=True, type=Path)
    parser.add_argument("--manifest", required=True, type=Path)
    parser.add_argument("--sql-out", required=True, type=Path)
    parser.add_argument("--media-plan-out", required=True, type=Path)
    return parser.parse_args(argv)


def main(argv: Optional[Sequence[str]] = None) -> int:
    args = parse_args(argv)
    manifest = load_manifest(args.manifest)
    sql, plan = generate(args.source_dir, manifest)
    args.sql_out.parent.mkdir(parents=True, exist_ok=True)
    args.media_plan_out.parent.mkdir(parents=True, exist_ok=True)
    args.sql_out.write_text(sql, encoding="utf-8")
    args.media_plan_out.write_text(json.dumps(plan, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("generated {} and {}".format(args.sql_out, args.media_plan_out))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
