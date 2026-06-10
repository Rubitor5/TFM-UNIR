import argparse
import boto3
import json
import re
from prompt_builder import build_prompt

MODEL_ID = "arn:aws:bedrock:eu-north-1:100002492253:inference-profile/eu.anthropic.claude-sonnet-4-5-20250929-v1:0"


def clean_llm_output(text: str) -> str:
    text = text.strip()
    text = text.replace("```json", "").replace("```", "")
    return text.strip()


def extract_json(text: str) -> dict:
    match = re.search(r"\{.*\}", text, re.DOTALL)
    if not match:
        raise ValueError("No JSON found in model output")
    return json.loads(match.group(0))


def normalize(data: dict) -> dict:
    findings = []

    def add(section, severity):
        for item in data.get(section, []) or []:

            findings.append({
                # identity
                "finding_id": item.get("finding_id"),

                # classification
                "triage_decision": "TRUE_POSITIVE",
                "severity_adjusted": severity,

                # scoring (safe defaults)
                "confidence": 0.9 if severity == "CRITICAL" else 0.75,

                # 🔥 enriched context (NEW)
                "title": item.get("title", ""),
                "priority": item.get("priority", ""),
                "risk_score": item.get("risk_score", None),
                "exploitability": item.get("exploitability", ""),
                "impact": item.get("impact", ""),
                "rationale": item.get("rationale", ""),

                # reasoning (normalized)
                "reasoning": (
                    item.get("rationale")
                    or item.get("recommendation")
                    or item.get("title")
                    or ""
                ),

                # action resolution chain (safe fallback)
                "recommended_action": (
                    item.get("remediation")
                    or item.get("recommended_action")
                    or item.get("recommendation")
                    or "NO_ACTION_PROVIDED"
                )
            })

    add("critical_findings", "CRITICAL")
    add("high_priority_findings", "HIGH")

    # false positives
    for item in data.get("false_positive_candidates", []) or []:
        findings.append({
            "finding_id": item.get("finding_id"),
            "triage_decision": "FALSE_POSITIVE",
            "severity_adjusted": "LOW",
            "confidence": 0.3,

            # keep context even for FP (important for audits)
            "reasoning": item.get("reason", ""),
            "title": item.get("title", ""),

            "recommended_action": "IGNORE"
        })

    return {"findings": findings}


def invoke(prompt):
    client = boto3.client("bedrock-runtime")

    response = client.invoke_model(
        modelId=MODEL_ID,
        body=json.dumps({
            "anthropic_version": "bedrock-2023-05-31",
            "max_tokens": 4000,
            "messages": [
                {"role": "user", "content": prompt}
            ]
        })
    )

    raw = response["body"].read().decode("utf-8")

    data = json.loads(raw)
    text = data["content"][0]["text"]

    text = clean_llm_output(text)
    parsed = extract_json(text)

    return normalize(parsed)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--input")
    parser.add_argument("--output")
    args = parser.parse_args()

    with open(args.input) as f:
        findings = json.load(f)

    prompt = build_prompt(findings)

    result = invoke(prompt)

    with open(args.output, "w") as f:
        json.dump(result, f, indent=2)


if __name__ == "__main__":
    main()