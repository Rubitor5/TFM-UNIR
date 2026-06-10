# Preview Environments

This directory contains preview environment configurations for ephemeral namespaces.

Each pull request can create a temporary namespace with a preview deployment using ArgoCD ApplicationSet.

## File Format

Create YAML files in this directory with the following structure:

```yaml
namespace: pr-<PR_NUMBER>
image:
  repository: <ECR_REPOSITORY_URL>
  tag: <COMMIT_SHA>
host: pr-<PR_NUMBER>.example.local
```

## Example

```yaml
namespace: pr-123
image:
  repository: <ACCOUNT_ID>.dkr.ecr.eu-north-1.amazonaws.com/project-microservice
  tag: abc1234
host: pr-123.example.local
```

## Automation

GitHub Actions should:

1. Create a new YAML file in this directory on each PR
2. Update the file with the latest image build on each push
3. Delete the file when the PR is closed/merged

This is handled by the ApplicationSet in `applicationsets/preview-envs.yaml`.
