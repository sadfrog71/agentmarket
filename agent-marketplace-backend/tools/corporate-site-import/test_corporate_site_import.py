#!/usr/bin/env python3
"""Standard-library tests for the corporate-site import preflight tools."""

from __future__ import annotations

import json
import shutil
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path


TOOLS_DIR = Path(__file__).resolve().parent
sys.path.insert(0, str(TOOLS_DIR))

import build_manifest  # noqa: E402
import content_overrides  # noqa: E402
import generate_seed_sql  # noqa: E402
import stage_media  # noqa: E402
import upgrade_preflight  # noqa: E402


class CorporateSiteManifestTests(unittest.TestCase):
    def setUp(self) -> None:
        self.temporary_directory = tempfile.TemporaryDirectory()
        self.root = Path(self.temporary_directory.name)
        self.source = self.root / "official-export"
        self.source.mkdir()

    def tearDown(self) -> None:
        self.temporary_directory.cleanup()

    def write_source(self, relative_path: str, content: bytes) -> Path:
        path = self.source / relative_path
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_bytes(content)
        return path

    def test_manifest_classifies_every_file_and_is_location_independent(self) -> None:
        self.write_source(
            "index.html",
            b'<link href="assets/site.css"><img src="assets/logo.svg"><a href="news-articles/a01.html">news</a>',
        )
        self.write_source("news-articles/a01.html", b"<p>news</p>")
        self.write_source("assets/site.css", b".hero { background: url('hero.webp'); }")
        self.write_source("assets/logo.svg", b"<svg></svg>")
        self.write_source("assets/hero.webp", b"webp-bytes")
        self.write_source("软件著作权证书/license.pdf", b"%PDF-test")
        self.write_source("qa/result.json", b'{"checked": true}')
        self.write_source("brand-spec.md", b"# authoring note")
        self.write_source(".DS_Store", b"finder metadata")
        self.write_source("misc.unknown", b"requires review")

        manifest = build_manifest.build_manifest(self.source)
        first_output = self.root / "manifest.json"
        inventory_output = self.root / "inventory.md"
        build_manifest.write_outputs(manifest, first_output, inventory_output)
        loaded = json.loads(first_output.read_text(encoding="utf-8"))

        self.assertNotIn(str(self.source.resolve()), first_output.read_text(encoding="utf-8"))
        self.assertEqual(10, loaded["summary"]["file_count"])
        self.assertEqual(1, loaded["summary"]["fixed_page_count"])
        self.assertEqual(1, loaded["summary"]["news_article_count"])
        self.assertEqual(1, loaded["summary"]["credential_pdf_count"])
        self.assertEqual(2, loaded["summary"]["media_file_count"])
        self.assertEqual(0, loaded["summary"]["unclassified_file_count"])
        self.assertEqual({"IMPORT", "ARCHIVE", "APPROVED_EXCLUSION"}, {entry["disposition"] for entry in loaded["files"]})

        by_path = {entry["legacy_path"]: entry for entry in loaded["files"]}
        self.assertEqual("text/html; charset=utf-8", by_path["/index.html"]["content_type"])
        self.assertEqual("image/svg+xml", by_path["/assets/logo.svg"]["content_type"])
        self.assertEqual("IMPORT", by_path["/软件著作权证书/license.pdf"]["disposition"])
        self.assertEqual("ARCHIVE", by_path["/qa/result.json"]["disposition"])
        self.assertEqual("APPROVED_EXCLUSION", by_path["/.DS_Store"]["disposition"])
        self.assertEqual(1, by_path["/assets/hero.webp"]["reference_count"])
        self.assertIn("固定页面 | 1", inventory_output.read_text(encoding="utf-8"))

        copied_source = self.root / "copied-export"
        shutil.copytree(self.source, copied_source)
        copied_manifest = build_manifest.build_manifest(copied_source)
        self.assertEqual(manifest, copied_manifest)


class UpgradePreflightTests(unittest.TestCase):
    def setUp(self) -> None:
        self.temporary_directory = tempfile.TemporaryDirectory()
        self.sql_dir = Path(self.temporary_directory.name) / "sql"
        self.sql_dir.mkdir()

    def tearDown(self) -> None:
        self.temporary_directory.cleanup()

    def write_upgrade(self, number: int, suffix: str = "site") -> Path:
        path = self.sql_dir / "marketplace-upgrade-{:03d}-{}.sql".format(number, suffix)
        path.write_text("-- upgrade {}\n".format(number), encoding="utf-8")
        return path

    def test_preflight_accepts_continuous_additive_target_and_hashes_it(self) -> None:
        self.write_upgrade(1, "business")
        target = self.write_upgrade(2, "site")
        report = upgrade_preflight.build_preflight_report(self.sql_dir, [target])

        self.assertTrue(report["ok"])
        self.assertEqual("none", report["database_interaction"])
        self.assertEqual([1, 2], report["continuous_numbering"]["actual_numbers"])
        self.assertEqual("marketplace-upgrade-002-site.sql", report["target_sql"][0]["path"])
        self.assertEqual(64, len(report["target_sql"][0]["sha256"]))

    def test_preflight_rejects_missing_numbers_and_non_tail_target(self) -> None:
        first = self.write_upgrade(1, "business")
        self.write_upgrade(3, "site")
        report = upgrade_preflight.build_preflight_report(self.sql_dir, [first])

        self.assertFalse(report["ok"])
        self.assertEqual([2], report["continuous_numbering"]["missing_numbers"])
        failure_codes = {failure["code"] for failure in report["failures"]}
        self.assertIn("MISSING_UPGRADE_NUMBERS", failure_codes)
        self.assertIn("TARGET_NOT_ADDITIVE_TAIL", failure_codes)

    def test_cli_writes_report_without_database_access(self) -> None:
        self.write_upgrade(1, "business")
        target = self.write_upgrade(2, "site")
        report_output = self.sql_dir.parent / "preflight.json"
        completed = subprocess.run(
            [
                sys.executable,
                str(TOOLS_DIR / "upgrade_preflight.py"),
                "--sql-dir",
                str(self.sql_dir),
                "--target-sql",
                str(target),
                "--report-out",
                str(report_output),
            ],
            check=False,
            capture_output=True,
            text=True,
        )

        self.assertEqual(0, completed.returncode, completed.stderr)
        report = json.loads(report_output.read_text(encoding="utf-8"))
        self.assertTrue(report["ok"])
        self.assertEqual("read_only_local_sql_scan", report["mode"])


class CorporateSiteSeedToolTests(unittest.TestCase):
    def setUp(self) -> None:
        self.temporary_directory = tempfile.TemporaryDirectory()
        self.root = Path(self.temporary_directory.name)

    def tearDown(self) -> None:
        self.temporary_directory.cleanup()

    def test_rewrite_media_normalizes_parent_path_without_escaping_source_root(self) -> None:
        media = {
            "/assets/logo.svg": {"sha256": "ignored"},
        }
        rewritten, references = generate_seed_sql.rewrite_media_urls(
            '<img src="../assets/logo.svg"><a href="../../outside.pdf">outside</a>',
            "/news-articles/a01.html",
            media,
        )

        self.assertIn("/api/open/site/v1/media/", rewritten)
        self.assertIn('href="../../outside.pdf"', rewritten)
        self.assertEqual(["/assets/logo.svg"], references)

    def test_empty_utf8_sql_literal_uses_a_valid_empty_string(self) -> None:
        self.assertEqual("''", generate_seed_sql.sql_utf8(""))
        self.assertNotIn("0x using", generate_seed_sql.sql_utf8(""))

    def test_approved_water_content_override_adds_smart_drainage_after_yanyun(self) -> None:
        body = (
            '<div class="submenu" id="sub-water"><a href="ai-os.html">衍智云 · 水务 AI OS<span>↗</span></a>'
            '<a href="yanyun.html">衍云 · 一体化水务平台<span>↗</span></a>'
            '<a href="yanshu.html">衍数 · 数据咨询与治理<span>↗</span></a></div>'
            '<p>以衍智云、衍云、衍数三条产品线，连接智能应用、业务运行与数据基础。</p>'
            '<div class="brand-nav"><a href="ai-os.html"><span>01</span><h2>衍智云</h2><p>水务 AI OS</p><b>↓</b></a>'
            '<a href="yanyun.html"><span>02</span><h2>衍云</h2><p>一体化水务平台</p><b>↓</b></a>'
            '<a href="yanshu.html"><span>03</span><h2>衍数</h2><p>数据咨询与治理</p><b>↓</b></a></div>'
            '<section class="section wrap" id="yanzhiyun"><p>remove me</p></section>'
            '<section class="section mist" id="yanyun"><p>keep me</p></section>'
            '<section class="section wrap" id="yanshu"><p>keep me too</p></section>'
            '<span>02 / 衍云</span><span>03 / 衍数</span>'
            '<a class="text-link" href="ai-os.html">衍智云 · 水务 AI OS<span aria-hidden="true">↗</span></a>'
            '<a class="text-link" href="yanyun.html">衍云 · 一体化水务平台<span aria-hidden="true">↗</span></a>'
            '<a class="text-link" href="yanshu.html">衍数 · 数据咨询与治理<span aria-hidden="true">↗</span></a>'
        )
        title, _description, rewritten = content_overrides.apply_content_overrides(
            "/water.html", "智慧水务 · 衍智云 / 衍云 / 衍数 — 平行数字", "", body
        )

        self.assertNotIn("衍智云", title + rewritten)
        self.assertIn("智慧水务 · 衍云 / 智慧排水 / 衍数", title)
        self.assertIn("以衍云、智慧排水、衍数三条产品线", rewritten)
        self.assertIn('<a href="yanyun.html"><span>01</span>', rewritten)
        self.assertIn('<a href="drainage.html"><span>02</span>', rewritten)
        self.assertIn('<a href="yanshu.html"><span>03</span>', rewritten)
        self.assertIn('<span>01 / 衍云</span>', rewritten)
        self.assertIn('id="drainage"', rewritten)
        self.assertLess(rewritten.index('href="yanyun.html"'), rewritten.index('href="drainage.html"'))
        self.assertLess(rewritten.index('href="drainage.html"'), rewritten.index('href="yanshu.html"'))

    def test_smart_drainage_menu_is_added_once_to_other_pages(self) -> None:
        body = (
            '<div class="submenu" id="sub-water">'
            '<a href="yanyun.html">衍云 · 一体化水务平台<span>↗</span></a>'
            '<a href="yanshu.html">衍数 · 数据咨询与治理<span>↗</span></a></div>'
        )

        _title, _description, rewritten = content_overrides.apply_content_overrides("/about.html", "关于平行", "", body)
        _title, _description, rewritten = content_overrides.apply_content_overrides("/about.html", "关于平行", "", rewritten)

        self.assertEqual(1, rewritten.count('href="drainage.html"'))
        self.assertLess(rewritten.index('href="yanyun.html"'), rewritten.index('href="drainage.html"'))
        self.assertLess(rewritten.index('href="drainage.html"'), rewritten.index('href="yanshu.html"'))

    def test_framework_override_targets_the_new_media_asset(self) -> None:
        _title, _description, rewritten = content_overrides.apply_content_overrides(
            "/yanyun.html", "衍云", "", 'src="assets/parallel-brand/water-business-framework.png"'
        )

        self.assertIn(content_overrides.REPLACEMENT_FRAMEWORK_ASSET, rewritten)
        self.assertNotIn('src="' + content_overrides.SUPERSEDED_FRAMEWORK_ASSET + '"', rewritten)

    def test_yanyun_override_removes_the_water_ai_cross_reference(self) -> None:
        _title, _description, rewritten = content_overrides.apply_content_overrides(
            "/yanyun.html", "衍云", "", "AI 与数据协同衔接衍智云智能能力和衍数数据治理服务。"
        )

        self.assertEqual("数据与应用协同衔接智能应用能力和衍数数据治理服务。", rewritten)

    def test_stage_media_is_hash_verified_and_idempotent(self) -> None:
        source = self.root / "source"
        media_root = self.root / "media"
        source_file = source / "assets" / "logo.svg"
        source_file.parent.mkdir(parents=True)
        source_file.write_bytes(b"<svg>logo</svg>")
        digest = stage_media.sha256_file(source_file)
        item = {
            "source_relative_path": "assets/logo.svg",
            "target_storage_key": "available/logo.svg",
            "public_id": "test-logo",
            "sha256": digest,
            "size_bytes": source_file.stat().st_size,
        }

        self.assertEqual({"copied": 1, "skipped": 0, "verified": 1}, stage_media.stage_media(source, media_root, [item]))
        self.assertEqual({"copied": 0, "skipped": 1, "verified": 1}, stage_media.stage_media(source, media_root, [item]))
        self.assertEqual(b"<svg>logo</svg>", (media_root / "available" / "logo.svg").read_bytes())

        unsafe = dict(item, target_storage_key="../outside.svg")
        with self.assertRaisesRegex(ValueError, "unsafe target storage key"):
            stage_media.stage_media(source, media_root, [unsafe])


if __name__ == "__main__":
    unittest.main()
