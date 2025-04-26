provider "aws" {
  region = "us-west-2" # or your preferred region
}

########################
# VPC + Networking
########################

resource "aws_vpc" "this" {
  cidr_block = "10.0.0.0/16"
  tags = {
    Name = "ecs-vpc"
  }
}

resource "aws_internet_gateway" "this" {
  vpc_id = aws_vpc.this.id
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.this.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true
  availability_zone       = "us-west-2a"
  tags = {
    Name = "ecs-public-subnet"
  }
}
resource "aws_subnet" "private_a" {
  vpc_id                  = aws_vpc.this.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "us-west-2a"
  map_public_ip_on_launch = false
  tags = {
    Name = "ecs-private-subnet-a"
  }
}

resource "aws_subnet" "private_b" {
  vpc_id                  = aws_vpc.this.id
  cidr_block              = "10.0.3.0/24"
  availability_zone       = "us-west-2b"
  map_public_ip_on_launch = false
  tags = {
    Name = "ecs-private-subnet-b"
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.this.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.this.id
  }
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

resource "aws_security_group" "ecs_instance_sg" {
  name        = "ecs-instance-sg"
  description = "Allow 8080 access"
  vpc_id      = aws_vpc.this.id

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Allow SSH access (22)"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["73.162.188.216/32"] # or restrict to your IP
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}


resource "aws_security_group" "rds_sg" {
  name        = "rds-sg"
  description = "Allow PostgreSQL traffic from ECS EC2"
  vpc_id      = aws_vpc.this.id

  ingress {
    description = "PostgreSQL"
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    security_groups = [aws_security_group.ecs_instance_sg.id] # Allow from ECS EC2 SG
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_subnet_group" "rds" {
  name       = "rds-subnet-group"
  subnet_ids = [
    aws_subnet.private_a.id,
    aws_subnet.private_b.id
  ] 
  tags = {
    Name = "RDS subnet group"
  }
}

resource "aws_db_instance" "postgres" {
  identifier              = "springboot-postgres"
  engine                  = "postgres"
  instance_class          = "db.t3.micro"
  allocated_storage       = 20
  db_name                    = "postgres"
  username                = "postgres"
  password                = var.db_password
  db_subnet_group_name    = aws_db_subnet_group.rds.name
  vpc_security_group_ids  = [aws_security_group.rds_sg.id]
  publicly_accessible     = false # <<< Private only
  skip_final_snapshot     = true
  deletion_protection     = false

  tags = {
    Name = "springboot-postgres-db"
  }
}

########################
# IAM Role for EC2
########################

resource "aws_iam_role" "ecs_instance_role" {
  name = "ecsInstanceRole1"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_instance_role_policy" {
  role       = aws_iam_role.ecs_instance_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ecs_instance_profile" {
  name = "ecsInstanceProfile"
  role = aws_iam_role.ecs_instance_role.name
}

########################
# ECS Cluster
########################

resource "aws_ecs_cluster" "this" {
  name = "springboot-ecs-cluster"
}

########################
# EC2 Instance for ECS
########################

resource "aws_instance" "ecs_instance" {
  ami                    = data.aws_ami.ecs_optimized_ami.id
  instance_type          = "t3.micro" # free tier eligible
  subnet_id              = aws_subnet.public.id
  associate_public_ip_address = true
  security_groups        = [aws_security_group.ecs_instance_sg.id]
  iam_instance_profile   = aws_iam_instance_profile.ecs_instance_profile.name
  key_name               = var.key_name # <-- Your SSH key name

  user_data = <<-EOF
              #!/bin/bash
              echo ECS_CLUSTER=${aws_ecs_cluster.this.name} >> /etc/ecs/ecs.config
              EOF

  tags = {
    Name = "ecs-instance"
  }
}

data "aws_ami" "ecs_optimized_ami" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-ecs-hvm-*-x86_64-ebs"]
  }
}

########################
# ECS Task Definition
########################

resource "aws_ecs_task_definition" "this" {
  family                   = "springboot-task"
  requires_compatibilities = ["EC2"]
  network_mode             = "bridge"
  cpu                      = "256"
  memory                   = "512"

  container_definitions = jsonencode([
    {
      name      = "springboot-container"
      image     = "public.ecr.aws/t6v7l3e1/fittlens-public:v0.0.2"
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
        }
      ]
environment = [
      {
        name  = "DB_URL"
        value = var.db_url
      },
      {
        name  = "DB_USERNAME"
        value = var.db_username
      },
      {
        name  = "DB_PASSWORD"
        value = var.db_password
      }
    ]
    }
  ])
}

########################
# ECS Service
########################

resource "aws_ecs_service" "this" {
  name            = "springboot-service"
  cluster         = aws_ecs_cluster.this.id
  task_definition = aws_ecs_task_definition.this.arn
  desired_count   = 1
  launch_type     = "EC2"

  deployment_controller {
    type = "ECS"
  }
}