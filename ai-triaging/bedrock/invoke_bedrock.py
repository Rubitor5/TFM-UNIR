import argparse
import boto3
import json
import re
from prompt_builder import build_prompt

MODEL_ID = "arn:aws:bedrock:eu-north-1:100002492253:inference-profile/eu.anthropic.claude-sonnet-4-5-20250929-v1:0"


def clean_llm_output(text: str) -> str:
    text = re.sub(r"```json", "", text)
    text = text.replace("```", "")
    return text.strip()


def invoke(prompt):
    client = boto3.client("bedrock-runtime")

    response = client.invoke_model(
        modelId=MODEL_ID,
        body=json.dumps({
            "anthropic_version": "bedrock-2023-05-31",
            "max_tokens": 4000,
            "messages": [
                {
                    "role": "user",
                    "content": prompt
                }
            ]
        })
    )

    raw = response["body"].read().decode("utf-8")
    data = json.loads(raw)

    text = data["content"][0]["text"]

    return clean_llm_output(text)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--input")
    parser.add_argument("--output")
    args = parser.parse_args()

    with open(args.input) as f:
        findings = json.load(f)

    prompt = build_prompt(findings)

    result = invoke(prompt)

    try:
        parsed = json.loads(result)
    except json.JSONDecodeError as e:
        raise ValueError(f"Model returned invalid JSON: {e}\nOutput:\n{result}")

    with open(args.output, "w") as f:
        json.dump(parsed, f, indent=2)


if __name__ == "__main__":
    main()