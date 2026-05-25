import json
import uuid

from severity_mapper import normalize_severity


def parse_semgrep(path):
    findings = []

    with open(path) as f:
        data = json.load(f)

    for result in data.get("results", []):
        findings.append({
            "finding_id": str(uuid.uuid4()),
            "tool": "semgrep",
            "rule_id": result.get("check_id"),
            "severity_original": result.get("extra", {}).get("severity", "INFO"),
            "severity_normalized": normalize_severity(
                result.get("extra", {}).get("severity", "INFO")
            ),
            "title": result.get("extra", {}).get("message", "Semgrep Finding"),
            "description": result.get("extra", {}).get("message", ""),
            "file": result.get("path"),
            "line": result.get("start", {}).get("line", 0),
            "code_snippet": result.get("extra", {}).get("lines", "")
        })

    return findings