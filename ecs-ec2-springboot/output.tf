output "ec2_public_ip" {
  description = "Public IP of ECS EC2 instance"
  value       = aws_instance.ecs_instance.public_ip
}
output "rds_endpoint" {
  description = "PostgreSQL RDS endpoint"
  value       = aws_db_instance.postgres.endpoint
}

output "rds_username" {
  description = "PostgreSQL Master Username"
  value       = aws_db_instance.postgres.username
}