import json
import os
import sys
from github import Github


def extract_pr_number():
    event_path = os.environ.get("GITHUB_EVENT_PATH")

    if event_path and os.path.exists(event_path):
        with open(event_path) as f:
            event = json.load(f)

        pr = event.get("pull_request")
        if pr and "number" in pr:
            return pr["number"]

    ref = os.environ.get("GITHUB_REF", "")
    parts = ref.split("/")

    for p in parts:
        if p.isdigit():
            return int(p)

    raise ValueError(f"Cannot extract PR number from GITHUB_REF: {ref}")


def safe(v, default="N/A"):
    if v is None or v == "":
        return default
    return v


def render_finding(f):
    return f"""
### {safe(f.get('title'), 'Untitled Finding')}

- **ID:** {safe(f.get('finding_id'))}
- **Decision:** {safe(f.get('triage_decision'))}
- **Severity:** {safe(f.get('severity_adjusted'))}
- **Confidence:** {safe(f.get('confidence'), 0)}
- **Priority:** {safe(f.get('priority'))}
- **Risk Score:** {safe(f.get('risk_score'))}
- **Exploitability:** {safe(f.get('exploitability'))}
- **Impact:** {safe(f.get('impact'))}

**Reasoning:**
{safe(f.get('reasoning'))}

**Rationale:**
{safe(f.get('rationale'))}

**Recommended Action:**
{safe(f.get('recommended_action'))}
"""


def main(path):
    token = os.environ["GITHUB_TOKEN"]
    repo_name = os.environ["GITHUB_REPOSITORY"]

    pr_number = extract_pr_number()

    g = Github(token)
    repo = g.get_repo(repo_name)
    pr = repo.get_pull(int(pr_number))

    with open(path) as f:
        data = json.load(f)

    findings = data.get("findings", [])

    body = "# 🔐 AI Security Triage Report\n\n"

    if not findings:
        body += "No findings returned by AI.\n"
    else:
        # group by severity
        grouped = {"CRITICAL": [], "HIGH": [], "MEDIUM": [], "INFO": [], "LOW": [], "FALSE_POSITIVE": []}

        for f in findings:
            sev = (f.get("severity_adjusted") or "INFO").upper()
            grouped.setdefault(sev, []).append(f)

        for severity in ["CRITICAL", "HIGH", "MEDIUM", "LOW", "INFO", "FALSE_POSITIVE"]:
            items = grouped.get(severity, [])
            if not items:
                continue

            body += f"\n## 🚨 {severity} ({len(items)})\n"

            for f in items:
                body += render_finding(f)
                body += "\n---\n"

    pr.create_issue_comment(body)


if __name__ == "__main__":
    main(sys.argv[1])