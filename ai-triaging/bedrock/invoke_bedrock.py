import argparse
import json
import re
import time
import requests

from prompt_builder import build_prompt

# CHANGE THIS to your EC2 endpoint
MODEL_URL = "http://13.50.224.107:11434/api/generate"

MAX_RETRIES = 3
BASE_DELAY = 2


def clean_output(text: str) -> str:
    text = re.sub(r"```json", "", text)
    text = text.replace("```", "")
    return text.strip()


def invoke(prompt: str) -> str:
    payload = {
        "model": "phi",
        "prompt": prompt,
        "stream": False
    }

    last_error = None

    for attempt in range(1, MAX_RETRIES + 1):
        try:
            response = requests.post(
                MODEL_URL,
                json=payload,
                timeout=120
            )

            response.raise_for_status()
            data = response.json()

            # ✅ PHI / OLLAMA FORMAT FIX
            text = data.get("response", "")

            if not text:
                raise ValueError("Empty response from model")

            cleaned = clean_output(text)

            return cleaned

        except Exception as e:
            last_error = e

            sleep_time = BASE_DELAY * (2 ** (attempt - 1))
            print(f"[MODEL retry] attempt {attempt} failed: {e}. retrying in {sleep_time}s")
            time.sleep(sleep_time)

    raise RuntimeError(f"Model failed after retries: {last_error}")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--input")
    parser.add_argument("--output")
    args = parser.parse_args()

    with open(args.input) as f:
        findings = json.load(f)

    prompt = build_prompt(findings)

    result = invoke(prompt)

    # IMPORTANT: validate JSON
    try:
        parsed = json.loads(result)
    except json.JSONDecodeError as e:
        raise ValueError(f"Model returned invalid JSON:\n{e}\n\nRAW OUTPUT:\n{result}")

    with open(args.output, "w") as f:
        json.dump(parsed, f, indent=2)


if __name__ == "__main__":
    main()