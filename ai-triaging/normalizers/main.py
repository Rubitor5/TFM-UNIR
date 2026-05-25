import argparse
import json

from semgrep_parser import parse_semgrep
from trivy_parser import parse_trivy
from sonarqube_parser import parse_sonarqube
from deduplicator import deduplicate


def main():
    parser = argparse.ArgumentParser()

    parser.add_argument("--semgrep")
    parser.add_argument("--trivy")
    parser.add_argument("--sonarqube")
    parser.add_argument("--output")

    args = parser.parse_args()

    findings = []

    findings.extend(parse_semgrep(args.semgrep))
    findings.extend(parse_trivy(args.trivy))
    findings.extend(parse_sonarqube(args.sonarqube))

    findings = deduplicate(findings)

    output = {
        "findings": findings
    }

    with open(args.output, "w") as f:
        json.dump(output, f, indent=2)


if __name__ == "__main__":
    main()