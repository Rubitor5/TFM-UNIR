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

    # fallback: workflow_dispatch or manual runs
    ref = os.environ.get("GITHUB_REF", "")

    # refs/pull/123/merge → extract 123
    parts = ref.split("/")
    for p in parts:
        if p.isdigit():
            return int(p)

    raise ValueError(f"Cannot extract PR number from GITHUB_REF: {ref}")


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
        for f in findings:
            body += f"## {f.get('triage_decision', 'UNKNOWN')}\n"
            body += f"- Severity: {f.get('severity_adjusted', 'N/A')}\n"
            body += f"- Confidence: {f.get('confidence', 0)}\n"
            body += f"- Action: {f.get('recommended_action', '')}\n\n"

    pr.create_issue_comment(body)


if __name__ == "__main__":
    main(sys.argv[1])