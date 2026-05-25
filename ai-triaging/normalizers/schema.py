from pydantic import BaseModel
from typing import List, Optional

class Finding(BaseModel):
    finding_id: str
    tool: str
    rule_id: str
    severity_original: str
    severity_normalized: str
    category: Optional[str]
    title: str
    description: str
    file: str
    line: int
    code_snippet: Optional[str]
    cwe: List[str] = []
    owasp: List[str] = []