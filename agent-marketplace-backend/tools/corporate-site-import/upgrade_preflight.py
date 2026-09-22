#!/usr/bin/env python3
"""Read-only preflight checks for additive marketplace upgrade SQL files.

This utility only scans local SQL files and optionally writes a JSON report.  It
never opens a network connection, reads credentials, or executes SQL.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import sys
from collections import Counter
from pathlib import Path
from typing import Dict, List, Optional, Sequence, Tuple


UPGRADE_NAME = re.compile(r"^marketplace-upgrade-(\d+)-.+\.sql$", re.IGNORECASE)


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as source:
        for chunk in iter(lambda: source.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def upgrade_number(path: Path) -> Optional[int]:
    match = UPGRADE_NAME.fullmatch(path.name)
    return int(match.group(1)) if match else None


def _script_record(sql_dir: Path, path: Path) -> Dict[str, object]:
    return {
        "path": path.resolve().relative_to(sql_dir.resolve()).as_posix(),
        "upgrade_number": upgrade_number(path),
        "size_bytes": path.stat().st_size,
        "sha256": sha256_file(path),
    }


def _failure(code: str, detail: str) -> Dict[str, str]:
    return {"code": code, "detail": detail}


def build_preflight_report(sql_dir: Path, target_sql: Sequence[Path]) -> Dict[str, object]:
    """Return a deterministic report; do not mutate the SQL directory or a DB."""
    sql_root = sql_dir.resolve()
    if not sql_root.is_dir():
        raise ValueError("SQL directory does not exist or is not a directory: {}".format(sql_dir))
    if not target_sql:
        raise ValueError("at least one --target-sql file is required")

    script_paths = sorted(
        (path for path in sql_root.iterdir() if path.is_file() and upgrade_number(path) is not None),
        key=lambda path: (upgrade_number(path), path.name),
    )
    records = [_script_record(sql_root, path) for path in script_paths]
    actual_numbers = [int(record["upgrade_number"]) for record in records]
    number_counts = Counter(actual_numbers)
    duplicate_numbers = sorted(number for number, count in number_counts.items() if count > 1)
    maximum = max(actual_numbers, default=0)
    expected_numbers = list(range(1, maximum + 1))
    missing_numbers = sorted(set(expected_numbers) - set(actual_numbers))

    failures: List[Dict[str, str]] = []
    if not records:
        failures.append(_failure("NO_UPGRADE_SCRIPTS", "no marketplace-upgrade-NNN-*.sql files found"))
    if missing_numbers:
        failures.append(
            _failure("MISSING_UPGRADE_NUMBERS", "missing upgrade numbers: {}".format(", ".join(map(str, missing_numbers))))
        )
    if duplicate_numbers:
        failures.append(
            _failure("DUPLICATE_UPGRADE_NUMBERS", "duplicate upgrade numbers: {}".format(", ".join(map(str, duplicate_numbers))))
        )

    target_records: List[Dict[str, object]] = []
    target_numbers: List[int] = []
    for requested_path in target_sql:
        path = requested_path.resolve()
        try:
            path.relative_to(sql_root)
        except ValueError:
            failures.append(_failure("TARGET_OUTSIDE_SQL_DIR", "target is outside SQL directory: {}".format(requested_path)))
            continue
        if not path.is_file():
            failures.append(_failure("TARGET_NOT_FOUND", "target SQL file not found: {}".format(path.relative_to(sql_root))))
            continue
        number = upgrade_number(path)
        if number is None:
            failures.append(
                _failure("INVALID_TARGET_NAME", "target does not match marketplace-upgrade-NNN-*.sql: {}".format(path.name))
            )
            continue
        target_records.append(_script_record(sql_root, path))
        target_numbers.append(number)

    if len(set(target_numbers)) != len(target_numbers):
        failures.append(_failure("DUPLICATE_TARGET_NUMBER", "target SQL files reuse an upgrade number"))
    if target_numbers:
        expected_tail = list(range(maximum - len(target_numbers) + 1, maximum + 1))
        if sorted(target_numbers) != expected_tail:
            failures.append(
                _failure(
                    "TARGET_NOT_ADDITIVE_TAIL",
                    "target upgrade numbers {} must be the current additive tail {}".format(
                        sorted(target_numbers), expected_tail
                    ),
                )
            )

    return {
        "schema_version": "corporate-site-upgrade-preflight/v1",
        "database_interaction": "none",
        "mode": "read_only_local_sql_scan",
        "sql_directory": ".",
        "continuous_numbering": {
            "ok": not missing_numbers and not duplicate_numbers and bool(records),
            "actual_numbers": actual_numbers,
            "expected_numbers": expected_numbers,
            "missing_numbers": missing_numbers,
            "duplicate_numbers": duplicate_numbers,
        },
        "existing_upgrade_scripts": records,
        "target_sql": target_records,
        "ok": not failures,
        "failures": failures,
    }


def parse_args(argv: Optional[List[str]] = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--sql-dir", required=True, type=Path, help="directory containing marketplace upgrade SQL")
    parser.add_argument(
        "--target-sql",
        action="append",
        required=True,
        type=Path,
        help="candidate additive upgrade SQL file; repeat for a contiguous batch",
    )
    parser.add_argument("--report-out", type=Path, help="optional JSON report path")
    return parser.parse_args(argv)


def main(argv: Optional[List[str]] = None) -> int:
    args = parse_args(argv)
    try:
        report = build_preflight_report(args.sql_dir, args.target_sql)
    except (OSError, ValueError) as error:
        print("upgrade preflight failed: {}".format(error), file=sys.stderr)
        return 2
    rendered = json.dumps(report, ensure_ascii=False, indent=2) + "\n"
    if args.report_out:
        args.report_out.parent.mkdir(parents=True, exist_ok=True)
        args.report_out.write_text(rendered, encoding="utf-8")
    print(rendered, end="")
    return 0 if report["ok"] else 1


if __name__ == "__main__":
    raise SystemExit(main())
