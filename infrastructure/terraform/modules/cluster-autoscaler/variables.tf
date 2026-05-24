variable "cluster_name" {
  type        = string
  description = "EKS cluster name"
}

variable "aws_region" {
  type        = string
  description = "AWS region"
}

variable "oidc_provider_arn" {
  type        = string
  description = "OIDC provider ARN from EKS"
}
