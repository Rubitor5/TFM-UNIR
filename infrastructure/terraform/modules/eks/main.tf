module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 21.0"

  name               = var.cluster_name
  kubernetes_version = "1.33"

  vpc_id     = var.vpc_id
  subnet_ids = var.subnet_ids

  addons = {
    coredns = {
      most_recent = true
      preserve    = true
    }
    kube-proxy = {
      most_recent = true
      preserve    = true
    }
    vpc-cni = {
      most_recent = true
      preserve    = true
      before_compute = true
      configuration_values = jsonencode({
        env = {
          ENABLE_PREFIX_DELEGATION = "true"
          WARM_IP_TARGET           = "1"
        }
      })
    }
  }

  enable_irsa = true
  enable_cluster_creator_admin_permissions = true
  endpoint_public_access = true

  eks_managed_node_groups = {
    default = {
      instance_types = ["t3.micro"]

      min_size     = 2
      max_size     = 4
      desired_size = 2

      iam_role_additional_policies = {
        AmazonEKSClusterAutoscalerPolicy = "arn:aws:iam::aws:policy/AutoScalingFullAccess"
      }

      tags = {
        "k8s.io/cluster-autoscaler/enabled" = "true"
        "k8s.io/cluster-autoscaler/${var.cluster_name}" = "owned"
      }
    }
  }
}