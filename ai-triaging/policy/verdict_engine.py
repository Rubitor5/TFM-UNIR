import json
import sys

FAIL_THRESHOLD = 0.85


def main(path):
    with open(path) as f:
        data = json.load(f)

    findings = data.get("findings", [])

    for finding in findings:
        severity = finding.get("severity_adjusted")
        confidence = finding.get("confidence", 0)

        if severity in ["CRITICAL", "HIGH"] and confidence >= FAIL_THRESHOLD:
            print("Blocking pipeline")
            sys.exit(1)

    print("Pipeline approved")


if __name__ == "__main__":
    main(sys.argv[1])