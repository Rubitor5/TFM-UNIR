import json
import uuid

from severity_mapper import normalize_severity


def parse_trivy(path):
    findings = []

    with open(path) as f:
        data = json.load(f)

    for result in data.get("Results", []):
        for vuln in result.get("Vulnerabilities", []):
            findings.append({
                "finding_id": str(uuid.uuid4()),
                "tool": "trivy",
                "rule_id": vuln.get("VulnerabilityID"),
                "severity_original": vuln.get("Severity"),
                "severity_normalized": normalize_severity(vuln.get("Severity")),
                "title": vuln.get("Title", "Trivy Finding"),
                "description": vuln.get("Description", ""),
                "file": result.get("Target", ""),
                "line": 0,
                "code_snippet": ""
            })

    return findings