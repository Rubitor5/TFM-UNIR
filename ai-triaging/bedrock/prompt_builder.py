def build_prompt(findings):
    return f"""
You are a security triage engine.

CRITICAL RULES:
- Output ONLY valid JSON
- No extra text
- No explanations
- No markdown
- No examples
- No reasoning

If you output anything else, it is invalid.

Return format:

{{
  "findings": [
    {{
      "finding_id": "string",
      "triage_decision": "TRUE_POSITIVE | FALSE_POSITIVE | NEEDS_REVIEW",
      "severity_adjusted": "CRITICAL | HIGH | MEDIUM | LOW | INFO",
      "confidence": 0.0,
      "reasoning": "string",
      "recommended_action": "string"
    }}
  ]
}}

INPUT:
{json.dumps(findings, indent=2)}
"""