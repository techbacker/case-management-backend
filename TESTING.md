# Case management API Testing Guide

This guide provides instructions for testing the case management endpoints.

## Available Endpoints

### 1. Create Task - POST `/api/tasks`
Creates a new task with the provided details.

**Request Body:**
```json
{
  "title": "Sample Task",
  "description": "This is a sample task",
  "status": "TODO",
  "caseId": "CASE-123456",
  "dueDateTime": "2024-12-31T23:59:59"
}
```

**Success Response (201):**
```json
{
  "id": 1,
  "title": "Sample Task",
  "description": "This is a sample task",
  "status": "TODO",
  "caseId": "CASE-123456",
  "dueDateTime": "2024-12-31T23:59:59",
  "createdDate": "2024-01-01T10:00:00",
  "updatedDate": "2024-01-01T10:00:00"
}
```

**Error Response (400):**
- Empty or null title returns 400 Bad Request

### 2. Get Task by ID - GET `/api/tasks/{id}`
Retrieves a specific task by its ID.

**Success Response (200):**
```json
{
  "id": 1,
  "title": "Sample Task",
  "description": "This is a sample task",
  "status": "TODO",
  "caseId": "CASE-123456",
  "dueDateTime": "2024-12-31T23:59:59",
  "createdDate": "2024-01-01T10:00:00",
  "updatedDate": "2024-01-01T10:00:00"
}
```

**Error Response (404):**
- Non-existent task ID returns 404 Not Found

### 3. Get All Tasks - GET `/api/tasks`
Retrieves all tasks.

**Success Response (200):**
```json
[
  {
    "id": 1,
    "title": "Task 1",
    "description": "First task",
    "status": "TODO",
    "caseId": "CASE-123456",
    "dueDateTime": "2024-12-31T23:59:59",
    "createdDate": "2024-01-01T10:00:00",
    "updatedDate": "2024-01-01T10:00:00"
  },
  {
    "id": 2,
    "title": "Task 2",
    "description": "Second task",
    "status": "IN_PROGRESS",
    "caseId": "CASE-654321",
    "dueDateTime": "2024-12-31T23:59:59",
    "createdDate": "2024-01-01T10:00:00",
    "updatedDate": "2024-01-01T10:00:00"
  }
]
```

### 4. Update Task Status - PUT `/api/tasks/{id}/status`
Updates the status of a specific task.

**Request Body:**
```json
{
  "status": "COMPLETED"
}
```

**Success Response (200):**
```json
{
  "id": 1,
  "title": "Sample Task",
  "description": "This is a sample task",
  "status": "COMPLETED",
  "caseId": "CASE-123456",
  "dueDateTime": "2024-12-31T23:59:59",
  "createdDate": "2024-01-01T10:00:00",
  "updatedDate": "2024-01-01T10:05:00"
}
```

**Error Responses:**
- 400 Bad Request: Empty or null status
- 404 Not Found: Non-existent task ID

### 5. Delete Task - DELETE `/api/tasks/{id}`
Deletes a specific task by its ID.

**Success Response (204):**
- Empty response body with 204 No Content

**Error Response (404):**
- Non-existent task ID returns 404 Not Found

## Manual Testing with curl

### Start the application
```bash
./gradlew bootRun
```

### Test scenarios:

1. **Create a task:**
```bash
curl -X POST http://localhost:4000/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Task",
    "description": "Testing the API",
    "status": "TODO",
    "caseId": "CASE-123456",
    "dueDateTime": "2024-12-31T23:59:59"
  }'
```

2. **Get all tasks:**
```bash
curl http://localhost:4000/api/tasks
```

3. **Get task by ID:**
```bash
curl http://localhost:4000/api/tasks/1
```

4. **Update task status:**
```bash
curl -X PUT http://localhost:4000/api/tasks/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "COMPLETED"}'
```

5. **Delete a task:**
```bash
curl -X DELETE http://localhost:4000/api/tasks/1
```

## Automated Testing

Run the unit tests to verify the controller behavior:

```bash
./gradlew test
```

The tests cover:
- Valid task creation
- Invalid task creation (null/empty title)
- Getting tasks by ID (existing and non-existing)
- Getting all tasks
- Updating task status (valid and invalid)
- Deleting tasks (existing and non-existing)

## Test Coverage

The TaskControllerUnitTest class provides comprehensive coverage for:
- ✅ POST /api/tasks - Create task (valid and invalid scenarios)
- ✅ GET /api/tasks/{id} - Get task by ID (found and not found)
- ✅ GET /api/tasks - Get all tasks
- ✅ PUT /api/tasks/{id}/status - Update task status (valid and invalid)
- ✅ DELETE /api/tasks/{id} - Delete task (existing and non-existing)

All endpoints are tested with proper HTTP status codes and response validation.