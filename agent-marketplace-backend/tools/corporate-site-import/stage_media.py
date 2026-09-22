#!/usr/bin/env python3
"""Copy a frozen corporate-site media plan into a private media volume safely.

Files are streamed into a private ``.staging`` directory, hash-verified, then
atomically promoted to the plan's ``available/`` storage key.  A failed run
never creates a public database record; it can be rerun and will skip bytewise
identical targets.  The tool never deletes a target file.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import os
import shutil
import sys
import uuid
from pathlib import Path
from typing import Dict, Iterable, List, Optional, Sequence


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as source:
        for chunk in iter(lambda: source.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def load_plan(path: Path) -> List[Dict[str, object]]:
    plan = json.loads(path.read_text(encoding="utf-8"))
    if plan.get("schema_version") != "corporate-site-media-copy-plan/v1":
        raise ValueError("unsupported media copy plan")
    items = plan.get("items")
    if not isinstance(items, list):
        raise ValueError("media copy plan has no items list")
    return items


def safe_target(root: Path, storage_key: str) -> Path:
    target = (root / storage_key).resolve()
    if not target.is_relative_to(root.resolve()) or not storage_key.startswith("available/"):
        raise ValueError("unsafe target storage key: {}".format(storage_key))
    return target


def stage_media(source_root: Path, media_root: Path, items: Iterable[Dict[str, object]], verify_only: bool = False) -> Dict[str, int]:
    source_root = source_root.resolve()
    media_root = media_root.resolve()
    if not source_root.is_dir():
        raise ValueError("source directory does not exist: {}".format(source_root))
    if not verify_only:
        media_root.mkdir(parents=True, exist_ok=True)
    counts = {"copied": 0, "skipped": 0, "verified": 0}
    for item in items:
        relative_path = str(item["source_relative_path"])
        expected_hash = str(item["sha256"])
        source = (source_root / relative_path).resolve()
        if not source.is_relative_to(source_root) or not source.is_file():
            raise ValueError("missing source file: {}".format(relative_path))
        source_hash = sha256_file(source)
        if source_hash != expected_hash:
            raise ValueError("source hash mismatch: {}".format(relative_path))
        if source.stat().st_size != int(item["size_bytes"]):
            raise ValueError("source size mismatch: {}".format(relative_path))
        counts["verified"] += 1
        if verify_only:
            continue
        target = safe_target(media_root, str(item["target_storage_key"]))
        if target.exists():
            if sha256_file(target) != expected_hash:
                raise ValueError("target hash conflict: {}".format(target.relative_to(media_root)))
            counts["skipped"] += 1
            continue
        target.parent.mkdir(parents=True, exist_ok=True)
        staging = media_root / ".staging" / (str(item["public_id"]) + "." + uuid.uuid4().hex)
        staging.parent.mkdir(parents=True, exist_ok=True)
        try:
            with source.open("rb") as input_stream, staging.open("xb") as output_stream:
                shutil.copyfileobj(input_stream, output_stream, length=1024 * 1024)
                output_stream.flush()
                os.fsync(output_stream.fileno())
            if sha256_file(staging) != expected_hash:
                raise ValueError("staging hash mismatch: {}".format(relative_path))
            os.replace(staging, target)
            counts["copied"] += 1
        finally:
            if staging.exists():
                staging.unlink()
    return counts


def parse_args(argv: Optional[Sequence[str]] = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source-dir", required=True, type=Path)
    parser.add_argument("--media-plan", required=True, type=Path)
    parser.add_argument("--media-root", required=True, type=Path)
    parser.add_argument("--verify-only", action="store_true")
    return parser.parse_args(argv)


def main(argv: Optional[Sequence[str]] = None) -> int:
    args = parse_args(argv)
    try:
        result = stage_media(args.source_dir, args.media_root, load_plan(args.media_plan), args.verify_only)
    except (OSError, ValueError, json.JSONDecodeError) as error:
        print("media staging failed: {}".format(error), file=sys.stderr)
        return 2
    print(json.dumps(result, ensure_ascii=False, sort_keys=True))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
