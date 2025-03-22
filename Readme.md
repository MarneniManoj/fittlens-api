### Product Requirements Document (PRD)
## 1. Introduction
### 1.1 Purpose
This document outlines the requirements for the Fitness Tracking API system, designed to help users manage their workout routines, equipment, and progress tracking.
### 1.2 Scope
The system will provide APIs for user management, equipment tracking, workout planning, and historical data access. The initial release focuses on core functionality with some features designated as out of scope for future implementation.

## 2. Data Models
```
{
  "User": {
    "uuid": "string - unique identifier",
    "name": "string - username (animal name by default)",
    "deviceId": "string - device identifier",
    "email": "string - user email (optional)",
    "token": "string - authentication token"
  },
  
  "Preferences": {
    "id": "string - unique identifier",
    "userId": "string - reference to User",
    "defaultGymId": "string - default gym for the user",
    "workoutDaysPerWeek": "integer - target workout frequency"
  },
  
  "Gym": {
    "id": "string - unique identifier",
    "userId": "string - reference to User",
    "equipment": ["array of Equipment objects available in this gym"]
  },
  
  "UserEquipment": {
    "id": "string - unique identifier",
    "userId": "string - reference to User",
    "recognizedEquipment": "Equipment - reference to standard equipment",
    "imageUsed": "string - URL or reference to the uploaded image"
  },
  
  "Equipment": {
    "id": "string - unique identifier",
    "name": "string - equipment name",
    "imageIcon": "string - URL or reference to equipment icon",
    "possibleExercises": ["array of Exercise objects possible with this equipment"],
    "userId": "string - reference to User (null for standard equipment)",
    "createDate": "timestamp - creation date",
    "updateDate": "timestamp - last update date"
  },
  
  "Exercise": {
    "id": "string - unique identifier",
    "name": "string - exercise name",
    "instructions": "string - how to perform the exercise",
    "requiredEquipment": ["array of Equipment objects - at least one required"],
    "targetMuscleGroup": ["array of Muscle objects - muscles targeted by this exercise"]
  },
  
  "WorkoutSession": {
    "id": "string - unique identifier",
    "userId": "string - reference to User",
    "exercises": ["array of Exercise objects included in this session"]
  },
  
  "Muscle": {
    "id": "string - unique identifier",
    "name": "string - muscle name",
    "image": "string - URL or reference to image highlighting the muscle"
  }
}
```

## 3. API Endpoints
```
## User Management

### User Registration
**POST /users/signup**
- **Purpose**: Create a new user account
- **Request Body**:
  ```json
  {
    "name": "string (optional)",
    "deviceId": "string (required)",
    "email": "string (optional)"
  }
  ```
- **Response**: User object with token
- **Notes**:
  - If name is not provided, system assigns random animal name
  - If requested username is taken, returns appropriate error message
  - Username validation uses Redis cache for quick lookup
  - Implements IP-based rate limiting

### User Lookup
**GET /users/username**
- **Purpose**: Retrieve user information by username
- **Query Parameters**: 
  - `username`: string (required)
- **Response**: User object
- **Notes**: 
  - Marked for deprecation in future releases
  - Currently required for user recognition

## Equipment Management

### Add Equipment
**POST /equipments**
- **Purpose**: Upload and recognize new user equipment
- **Request Body**:
  ```json
  {
    "gymId": "string (required)",
    "userEquipmentImage": "file (required)"
  }
  ```
- **Response**: Equipment object
- **Notes**:
  - System will process the image to recognize the equipment
  - Deduplication should prevent adding identical equipment twice

### List Equipment
**GET /equipments**
- **Purpose**: Retrieve all equipment for the authenticated user
- **Query Parameters**:
  - `gymId`: string (optional) - filter by gym
- **Response**: Array of Equipment objects

### Delete Equipment
**DELETE /equipments**
- **Purpose**: Remove equipment from user's collection
- **Request Body**:
  ```json
  {
    "equipmentId": "string (required)"
  }
  ```
- **Response**: Success message

## Workout Management

### Today's Workout
**GET /workouts/today**
- **Purpose**: Retrieve exercises planned for today
- **Response**: Array of Exercise objects

### Specific Workout
**GET /workouts/workoutId**
- **Purpose**: Retrieve a specific workout by ID
- **Path Parameters**:
  - `workoutId`: string (required)
- **Response**: Array of Exercise objects

## History Management

### Workout History
**GET /history/workouts**
- **Purpose**: Retrieve user's workout history
- **Query Parameters**:
  - `startDate`: string (optional) - ISO date format
  - `endDate`: string (optional) - ISO date format
  - `limit`: integer (optional) - default 20
  - `offset`: integer (optional) - default 0
- **Response**: Array of Workout objects
```



# fittlens-api
