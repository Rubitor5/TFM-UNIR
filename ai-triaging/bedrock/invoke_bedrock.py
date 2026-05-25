import argparse
import boto3
import json

from prompt_builder import build_prompt

MODEL_ID = "anthropic.claude-sonnet-4-5-20250929-v1:0"


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

    body = json.loads(response["body"].read())

    return body["content"][0]["text"]


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
        f.write(result)


if __name__ == "__main__":
    main()