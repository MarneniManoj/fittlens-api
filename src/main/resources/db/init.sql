-- Create database if not exists
CREATE DATABASE IF NOT EXISTS fittlens;

-- Create sequences
CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS equipment_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS workout_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS exercise_seq START WITH 1 INCREMENT BY 1;

-- Create tables with proper constraints and indexes
CREATE TABLE IF NOT EXISTS users (
    uuid VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    device_id VARCHAR(255),
    email VARCHAR(255),
    token VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS equipment (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image_icon VARCHAR(255),
    gym_id VARCHAR(36),
    user_uuid VARCHAR(36) REFERENCES users(uuid),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exercise (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    instructions TEXT,
    target_muscle_groups TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workout_session (
    id VARCHAR(36) PRIMARY KEY,
    user_uuid VARCHAR(36) REFERENCES users(uuid),
    scheduled_date TIMESTAMP,
    exercises JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_user_name ON users(name);
CREATE INDEX IF NOT EXISTS idx_equipment_user ON equipment(user_uuid);
CREATE INDEX IF NOT EXISTS idx_workout_user_date ON workout_session(user_uuid, scheduled_date); 