def deduplicate(findings):
    unique = {}

    for finding in findings:
        key = (
            finding["file"],
            finding["line"],
            finding["rule_id"]
        )

        unique[key] = finding

    return list(unique.values())