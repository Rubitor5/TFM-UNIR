import json
import os
from github import Github


def main(path):
    token = os.environ["GITHUB_TOKEN"]
    repo_name = os.environ["GITHUB_REPOSITORY"]

    event_path = os.environ.get("GITHUB_EVENT_PATH")

    if event_path:
        with open(event_path) as f:
            event = json.load(f)
        pr_number = event["pull_request"]["number"]
    else:
        pr_number = os.environ["GITHUB_REF"].split("/")[-2]

    g = Github(token)
    repo = g.get_repo(repo_name)
    pr = repo.get_pull(int(pr_number))

    with open(path) as f:
        data = json.load(f)

    findings = data.get("findings", [])

    body = "# 🔐 AI Security Triage Report\n\n"

    for f in findings:
        body += f"## {f.get('triage_decision', 'UNKNOWN')}\n"
        body += f"- Severity: {f.get('severity_adjusted', 'N/A')}\n"
        body += f"- Confidence: {f.get('confidence', 0)}\n"
        body += f"- Reasoning: {f.get('reasoning', '')}\n"
        body += f"- Action: {f.get('recommended_action', '')}\n\n"

    pr.create_issue_comment(body)


if __name__ == "__main__":
    import sys
    main(sys.argv[1])