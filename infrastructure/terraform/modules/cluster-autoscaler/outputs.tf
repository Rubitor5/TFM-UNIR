output "iam_role_arn" {
  value       = module.cluster_autoscaler_irsa.iam_role_arn
  description = "IAM role ARN for cluster autoscaler"
}

output "helm_release_status" {
  value       = helm_release.cluster_autoscaler.status
  description = "Helm release status"
}
