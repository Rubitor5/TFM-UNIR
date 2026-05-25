import json
import uuid

from severity_mapper import normalize_severity


def parse_sonarqube(path):
    findings = []

    with open(path) as f:
        data = json.load(f)

    for issue in data.get("issues", []):
        findings.append({
            "finding_id": str(uuid.uuid4()),
            "tool": "sonarqube",
            "rule_id": issue.get("rule"),
            "severity_original": issue.get("severity"),
            "severity_normalized": normalize_severity(issue.get("severity")),
            "title": issue.get("message"),
            "description": issue.get("message"),
            "file": issue.get("component"),
            "line": issue.get("line", 0),
            "code_snippet": ""
        })

    return findings