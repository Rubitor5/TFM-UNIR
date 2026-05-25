import json
import os
from github import Github


def main(path):
    token = os.environ["GITHUB_TOKEN"]
    repo_name = os.environ["GITHUB_REPOSITORY"]

    with open(os.environ["GITHUB_EVENT_PATH"]) as f:
        event = json.load(f)

    pr_number = event["pull_request"]["number"]

    g = Github(token)
    repo = g.get_repo(repo_name)
    pr = repo.get_pull(int(pr_number))


    with open(path) as f:
        content = f.read().strip()

    if not content:
        raise ValueError("AI results file is empty")

    data = json.loads(content)

    findings = data.get("findings", [])

    body = "# 🔐 AI Security Triage Report\n\n"

    for finding in findings:
        body += f"## {finding.get('triage_decision', 'UNKNOWN')}\n"
        body += f"- Severity: {finding.get('severity_adjusted', 'N/A')}\n"
        body += f"- Confidence: {finding.get('confidence', 0)}\n"
        body += f"- Reasoning: {finding.get('reasoning', 'N/A')}\n\n"

    if not findings:
        body += "No findings returned by AI.\n"

    pr.create_issue_comment(body)


if __name__ == "__main__":
    import sys
    main(sys.argv[1])