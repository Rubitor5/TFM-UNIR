import argparse
import boto3
import json
import re
import time
import botocore.exceptions
from prompt_builder import build_prompt

MODEL_ID = "arn:aws:bedrock:eu-north-1:100002492253:inference-profile/eu.anthropic.claude-sonnet-4-5-20250929-v1:0"

MAX_RETRIES = 3
BASE_DELAY = 2


def clean_llm_output(text: str) -> str:
    text = re.sub(r"```json", "", text)
    text = text.replace("```", "")
    return text.strip()


def invoke(prompt):
    client = boto3.client("bedrock-runtime")

    payload = {
        "anthropic_version": "bedrock-2023-05-31",
        "max_tokens": 4000,
        "messages": [
            {
                "role": "user",
                "content": prompt
            }
        ]
    }

    last_error = None

    for attempt in range(1, MAX_RETRIES + 1):
        try:
            response = client.invoke_model(
                modelId=MODEL_ID,
                body=json.dumps(payload),
                timeout=120
            )

            raw = response["body"].read().decode("utf-8")

            if not raw:
                raise ValueError("Empty response from Bedrock")

            data = json.loads(raw)

            text = data["content"][0]["text"]

            cleaned = clean_llm_output(text)

            if not cleaned:
                raise ValueError("Empty model output after cleaning")

            return cleaned

        except (botocore.exceptions.ReadTimeoutError,
                botocore.exceptions.EndpointConnectionError,
                ValueError,
                json.JSONDecodeError) as e:

            last_error = e

        except botocore.exceptions.ClientError as e:
            last_error = e

            # don't retry auth / config errors
            code = e.response["Error"]["Code"]
            if code in ["AccessDeniedException", "ValidationException"]:
                raise

        sleep_time = BASE_DELAY * (2 ** (attempt - 1))
        print(f"[Bedrock retry] attempt {attempt} failed: {last_error}. retrying in {sleep_time}s")
        time.sleep(sleep_time)

    raise RuntimeError(f"Bedrock failed after {MAX_RETRIES} retries: {last_error}")


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
        raise ValueError(f"Model returned invalid JSON: {e}\n\nRAW OUTPUT:\n{result}")

    with open(args.output, "w") as f:
        json.dump(parsed, f, indent=2)


if __name__ == "__main__":
    main()