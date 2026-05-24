data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

module "eks" {
  source = "./modules/eks"

  cluster_name = var.cluster_name
  vpc_id       = data.aws_vpc.default.id
  subnet_ids   = data.aws_subnets.default.ids
}

module "ecr" {
  source = "./modules/ecr"

  repository_name = "project-microservice"
}

module "argocd" {
  source = "./modules/argocd"

  depends_on = [null_resource.cluster_ready, module.eks]
}

module "cluster_autoscaler" {
  source = "./modules/cluster-autoscaler"

  cluster_name      = var.cluster_name
  aws_region        = var.aws_region
  oidc_provider_arn = module.eks.oidc_provider_arn

  depends_on = [null_resource.cluster_ready, module.eks]
}
