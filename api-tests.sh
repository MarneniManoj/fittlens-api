#!/bin/bash

# Set the API URL
API_URL=https://api.fittlens.com

echo "\nUser Management APIs"
echo "-------------------"

# Register a new user
echo "\nRegistering new user..."
curl --location "${API_URL}/users/signup" \
--header 'Content-Type: application/json' \
--data '{
    "name": "TestUser",
    "deviceId": "device-123",
    "email": "test@example.com"
}'

# Get user by username
echo "\nGetting user details..."
curl --location "${API_URL}/users/TestUser"

echo "\nEquipment Management APIs"
echo "------------------------"

USER_ID=4949f5c0-3654-4aed-b0bd-1fc77f03fdb8
# Create equipment without image
echo "\nCreating equipment without image..."
curl --location "${API_URL}/equipments/create" \
--header 'Content-Type: application/json' \
--header "User-Id: ${USER_ID}" \
--data '{
    "name": "Dumbbell 20kg",
    "imageIcon": "https://example.com/dumbbell.jpg",
    "gymId": "gym-123"
}'

# Add equipment with image
echo "\nAdding equipment with image..."
curl --location "${API_URL}/equipments" \
--header "User-Id: ${USER_ID}" \
--form 'userEquipmentImage=@"/Users/manojmarneni/Desktop/test-img.png"' \
--form 'gymId="gym-123"'

# List all equipment
echo "\nListing all equipment..."
curl --location "${API_URL}/equipments" \
--header "User-Id: ${USER_ID}"

# List equipment filtered by gym
echo "\nListing equipment for specific gym..."
curl --location "${API_URL}/equipments?gymId=gym-123" \
--header "User-Id: ${USER_ID}"

# Delete equipment
echo "\nDeleting equipment..."
curl --location --request DELETE "${API_URL}/equipments/equipment-123" \
--header "User-Id: ${USER_ID}"

echo "\nWorkout Management APIs"
echo "----------------------"

# Get today's workout
echo "\nGetting today's workout..."
curl --location "${API_URL}/workouts/today" \
--header "User-Id: ${USER_ID}"

# Get specific workout
echo "\nGetting specific workout..."
curl --location "${API_URL}/workouts/workout-123" \
--header "User-Id: ${USER_ID}"

# Get workout history with parameters
echo "\nGetting workout history with parameters..."
curl --location "${API_URL}/workouts/history?startDate=2024-03-01T00:00:00&endDate=2024-03-20T23:59:59&limit=20&offset=0" \
--header "User-Id: ${USER_ID}"

# Get workout history with defaults
echo "\nGetting workout history with defaults..."
curl --location "${API_URL}/workouts/history" \
--header "User-Id: ${USER_ID}"

echo "\nTests completed!" 