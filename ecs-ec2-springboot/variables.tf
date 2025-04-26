variable "key_name" {
  description = "SSH key name to access EC2 (must exist in AWS)"
  type        = string
}

variable "db_url" {
  description = "Database connection URL including jdbc driver"
  type        = string
}

variable "db_username" {
  description = "Database username"
  type        = string
}

variable "db_password" {
  description = "Database password"
  type        = string
  sensitive   = true
}