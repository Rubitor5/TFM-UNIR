import json
import os

from github import Github


def main(path):
    token = os.environ["GH_TOKEN"]
    

    repo_name = os.environ["GITHUB_REPOSITORY"]

    pr_number = os.environ["GITHUB_REF"].split("/")[-2]

    g = Github(token)

    repo = g.get_repo(repo_name)

    pr = repo.get_pull(int(pr_number))

    with open(path) as f:
        data = json.load(f)

    body = "# AI Security Triage Report\n\n"

    for finding in data.get("findings", []):
        body += f"## {finding['triage_decision']}\n"
        body += f"Severity: {finding['severity_adjusted']}\n"
        body += f"Confidence: {finding['confidence']}\n"
        body += f"Reasoning: {finding['reasoning']}\n\n"

    pr.create_issue_comment(body)


if __name__ == "__main__":
    import sys
    main(sys.argv[1])