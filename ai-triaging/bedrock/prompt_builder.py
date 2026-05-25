import json


def build_prompt(findings):
    return f"""
You are an Application Security AI Triage Agent.

Analyze the following findings.

Return JSON only.

Findings:
{json.dumps(findings, indent=2)}
"""