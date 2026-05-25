import json
import sys

FAIL_THRESHOLD = 0.85


def main(path):
    try:
        with open(path) as f:
            content = f.read().strip()

        if not content:
            print("ERROR: AI results file is empty")
            sys.exit(1)

        data = json.loads(content)

    except Exception as e:
        print(f"ERROR: Failed to parse AI results -> {e}")
        sys.exit(1)

    findings = data.get("findings", [])

    if not findings:
        print("WARNING: No findings returned by AI")
        sys.exit(0)

    block = False

    for finding in findings:
        severity = (finding.get("severity_adjusted") or "").upper()
        confidence = float(finding.get("confidence", 0))

        # safer policy: severity is primary, confidence is secondary
        if severity == "CRITICAL":
            block = True
        elif severity == "HIGH" and confidence >= FAIL_THRESHOLD:
            block = True

    if block:
        print("❌ Blocking pipeline due to security findings")
        sys.exit(1)

    print("✅ Pipeline approved")
    sys.exit(0)


if __name__ == "__main__":
    main(sys.argv[1])