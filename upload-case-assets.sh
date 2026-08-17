#!/usr/bin/env bash
set -euo pipefail

# 批量上传水务案例素材，并生成写回 biz_agent 的 SQL。
#
# 使用示例：
#   TOKEN='从管理后台浏览器 Local Storage 复制的 token' \
#   API_BASE_URL='http://localhost:8080' \
#   PUBLIC_BASE_URL='https://你的公开域名' \
#   bash upload-case-assets.sh
#
# 注意：
# 1. 该脚本只上传文件并生成 SQL，不自动执行数据库写入。
# 2. PUBLIC_BASE_URL 应该是浏览器可以访问 /profile/** 的地址；不填时使用上传接口返回的 url。
# 3. 上传接口返回的 fileName 形如 /profile/upload/2026/08/17/xxx.png。

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ASSET_ROOT="${ASSET_ROOT:-${SCRIPT_DIR}/智能体市场素材}"
API_BASE_URL="${API_BASE_URL:-http://localhost:8080}"
UPLOAD_ENDPOINT="${UPLOAD_ENDPOINT:-${API_BASE_URL%/}/common/upload}"
PUBLIC_BASE_URL="${PUBLIC_BASE_URL:-}"
OUTPUT_SQL="${OUTPUT_SQL:-${SCRIPT_DIR}/agent-marketplace-backend/sql/marketplace-case-images-update-20260817.sql}"
TOKEN="${TOKEN:-}"

if [[ -z "$TOKEN" ]]; then
  echo "缺少 TOKEN：请从已登录的管理后台复制 Token 后，通过环境变量传入。" >&2
  exit 1
fi

if [[ ! -d "$ASSET_ROOT" ]]; then
  echo "素材目录不存在：$ASSET_ROOT" >&2
  exit 1
fi

TMP_DIR="$(mktemp -d)"
UPLOAD_TSV="${TMP_DIR}/uploaded.tsv"
trap 'rm -rf "$TMP_DIR"' EXIT

# 编码|案例名|素材相对路径；第一张作为 cover_url，其余图片嵌入详情说明。
ASSETS=(
  'WATER_AI_CUSTOMER_SERVICE|AI智能客服|10-ai-customer-service/customer-service-screen.png|10-ai-customer-service/customer-service-screen-2.png'
  'WATER_CUSTOMER_WATER_ALERT|客户异常水量预警|09-water-alert/alert-flow.png|09-water-alert/alert-trend.png'
  'WATER_CUSTOMER_SERVICE_REPORT|AI客服报表自动化|11-customer-report/report-dashboard.png|11-customer-report/report-screen.png'
  'WATER_DMA_LEAKAGE_CONTROL|AI驱动的漏损控制体系|04-dma-leakage/leakage-map.png|04-dma-leakage/leakage-analysis.png'
  'WATER_DRONE_PIPELINE_PATROL|吴江区供水管网无人机AI巡检应用|03-drone-patrol/patrol-map.png|03-drone-patrol/patrol-dashboard.png'
  'WATER_ALGAE_DETECTION|AI藻类检测系统|01-ai-algae/ai-algae-result.png|01-ai-algae/ai-algae-dashboard.png'
  'WATER_LEAK_FORM_AUTOMATION|月度检漏表单自动化|07-leak-form/form-automation-console.png'
  'WATER_PLANT_ENERGY_CONTROL|AI节能技术在水厂的应用|06-water-plant-energy/control-architecture.png|06-water-plant-energy/energy-chart.png'
  'WATER_TEST_REPORT_AUDIT|AI全流程智能检测报告审核系统|02-report-audit/report-review-screen.png'
  'WATER_METER_VISION_RECOGNITION|AI抄表智能识别模型|08-meter-recognition/meter-recognition-a.png|08-meter-recognition/meter-recognition-b.png'
  'WATER_SECONDARY_SUPPLY_FAULT|AI智慧化停水故障诊断|05-secondary-supply/fault-table.png|05-secondary-supply/diagnostic-flow.png'
)

# 先检查全部本地文件，避免上传到一半才发现素材缺失。
for item in "${ASSETS[@]}"; do
  IFS='|' read -r agent_code agent_name primary rest <<< "$item"
  for relative_path in "$primary" ${rest//|/ }; do
    if [[ ! -f "$ASSET_ROOT/$relative_path" ]]; then
      echo "素材文件不存在：$ASSET_ROOT/$relative_path" >&2
      exit 1
    fi
  done
done

upload_one() {
  local path="$1"
  local response parsed status_code file_name returned_url public_url

  response="$(curl -fsS \
    -H "Authorization: Bearer ${TOKEN}" \
    -F "file=@${path}" \
    "$UPLOAD_ENDPOINT")"

  parsed="$(python3 -c '
import json, sys
data = json.load(sys.stdin)
if data.get("code") != 200:
    raise SystemExit(data.get("msg", "上传接口返回失败"))
print("{}\t{}\t{}".format(data.get("fileName", ""), data.get("url", ""), data.get("originalFilename", "")))
' <<< "$response")"

  IFS=$'\t' read -r file_name returned_url original_filename <<< "$parsed"
  if [[ -z "$file_name" ]]; then
    echo "上传成功但没有返回 fileName：$path" >&2
    exit 1
  fi

  if [[ -n "$PUBLIC_BASE_URL" ]]; then
    public_url="${PUBLIC_BASE_URL%/}${file_name}"
  else
    public_url="$returned_url"
  fi

  printf '%s\t%s\t%s\t%s\n' "$file_name" "$public_url" "$original_filename" "$path" >> "$UPLOAD_TSV"
  echo "已上传：$path -> $public_url"
}

for item in "${ASSETS[@]}"; do
  IFS='|' read -r agent_code agent_name primary rest <<< "$item"
  upload_one "$ASSET_ROOT/$primary"
  for relative_path in "$primary" ${rest//|/ }; do
    if [[ "$relative_path" != "$primary" ]]; then
      upload_one "$ASSET_ROOT/$relative_path"
    fi
  done
done

python3 - "$UPLOAD_TSV" "$OUTPUT_SQL" "${ASSETS[@]}" <<'PY'
from pathlib import Path
import sys

uploaded_tsv = Path(sys.argv[1])
output_sql = Path(sys.argv[2])
asset_rows = sys.argv[3:]

def sql_quote(value: str) -> str:
    return "'" + value.replace("'", "''") + "'"

uploaded = {}
for line in uploaded_tsv.read_text().splitlines():
    file_name, public_url, original_name, local_path = line.split("\t", 3)
    uploaded[local_path] = {
        "file_name": file_name,
        "public_url": public_url,
        "original_name": original_name,
    }

out = []
out.extend([
    "-- 水务 AI 案例图片 URL 写回脚本",
    "-- 由 upload-case-assets.sh 根据实际上传结果生成。",
    "-- 执行前请先确认所有 URL 可以从前台浏览器访问。",
    "USE `ry-vue`;",
    "SET NAMES utf8mb4;",
    "START TRANSACTION;",
    "",
])

agent_codes = []
for row in asset_rows:
    agent_code, agent_name, primary, *extra = row.split("|")
    relative_paths = [primary] + [x for x in extra if x]
    records = []
    for relative_path in relative_paths:
        key = str(Path(uploaded_tsv.parent.parent.parent) / "unused")
        # uploaded.tsv 中记录的是脚本实际传给 curl 的绝对路径；这里按后缀和素材相对路径定位。
        matches = [v for k, v in uploaded.items() if k.endswith("/智能体市场素材/" + relative_path)]
        if not matches:
            raise SystemExit(f"找不到上传结果：{relative_path}")
        records.append(matches[0])

    urls = [x["public_url"] for x in records]
    cover_url = urls[0]
    marker = f"<!-- CASE_ASSET:{agent_code} -->"
    image_html = [marker, "<hr><p><strong>项目截图</strong></p>"]
    for index, url in enumerate(urls, 1):
        image_html.append(f'<p><img src="{url}" alt="{agent_name}项目截图{index}" style="max-width:100%;height:auto;"></p>')
    image_html.append(f"<!-- /CASE_ASSET:{agent_code} -->")
    html_block = "".join(image_html)

    json_array = "JSON_ARRAY(" + ", ".join(sql_quote(x) for x in urls) + ")"
    out.extend([
        f"-- {agent_code} {agent_name}",
        "UPDATE biz_agent",
        f"SET cover_url = {sql_quote(cover_url)},",
        "    description = CASE",
        f"        WHEN INSTR(COALESCE(description, ''), {sql_quote(marker)}) = 0",
        f"        THEN CONCAT(COALESCE(description, ''), {sql_quote(html_block)})",
        "        ELSE description",
        "    END,",
        f"    ext_json = JSON_SET(COALESCE(ext_json, JSON_OBJECT()), '$.coverUrl', {sql_quote(cover_url)}, '$.imageUrls', {json_array}),",
        "    update_by = 'asset_import',",
        "    update_time = CURRENT_TIMESTAMP",
        f"WHERE agent_code = {sql_quote(agent_code)} AND del_flag = '0';",
        "",
    ])
    agent_codes.append(agent_code)

in_list = ", ".join(sql_quote(x) for x in agent_codes)
out.extend([
    "-- 写回校验：应返回 11 条记录，cover_url 不应为空。",
    "SELECT agent_code, agent_name, publish_status, cover_url, JSON_EXTRACT(ext_json, '$.imageUrls') AS image_urls",
    f"FROM biz_agent WHERE agent_code IN ({in_list}) ORDER BY sort_no;",
    "",
    "-- 确认图片 URL 和校验结果无误后，在同一连接执行：",
    "-- COMMIT;",
    "-- 如发现问题，执行：",
    "-- ROLLBACK;",
    "",
])

output_sql.parent.mkdir(parents=True, exist_ok=True)
output_sql.write_text("\n".join(out), encoding="utf-8")
print(f"已生成 SQL：{output_sql}")
PY

echo
echo "全部上传完成。请先抽查生成 SQL 中的公开 URL，再执行："
echo "  $OUTPUT_SQL"
