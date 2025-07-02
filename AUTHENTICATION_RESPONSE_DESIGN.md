# Flexible AuthenticationResponse Design

## Overview

The `AuthenticationResponse` has been enhanced to include user-specific data (`AdminResponseDTO` or `StudentResponseDTO`) based on the user type, providing a flexible and type-safe design.

## Features

### 🔄 **Polymorphic User Data**
- Automatically includes appropriate user data based on user type
- Supports `AdminResponseDTO` for ADMIN/SUPERADMIN users
- Supports `StudentResponseDTO` for STUDENT users
- No user data for VIEWER users (basic user info only)

### 🏗️ **Type-Safe Design**
- Jackson annotations for proper JSON serialization
- Type information included in JSON response
- Helper methods for type checking and data access

### 🛠️ **Utility Builder**
- `AuthenticationResponseBuilder` utility class
- Clean, readable code for building responses
- Handles all user types automatically

## Response Structure

### For Admin/SuperAdmin Users:
```json
{
  "token": "jwt_token_here",
  "refreshToken": "refresh_token_here",
  "userType": "ADMIN",
  "email": "admin@example.com",
  "fullName": "John Doe",
  "userData": {
    "type": "ADMIN",
    "userResponseDTO": {
      "userId": "uuid",
      "firstName": "John",
      "lastName": "Doe",
      "email": "admin@example.com",
      "userType": "ADMIN"
    },
    "adminId": "admin_uuid",
    "specializationResponseDTO": {
      "specializationId": "spec_uuid",
      "specializationName": "Computer Science"
    }
  }
}
```

### For Student Users:
```json
{
  "token": "jwt_token_here",
  "refreshToken": "refresh_token_here",
  "userType": "STUDENT",
  "email": "student@example.com",
  "fullName": "Jane Smith",
  "userData": {
    "type": "STUDENT",
    "userResponseDTO": {
      "userId": "uuid",
      "firstName": "Jane",
      "lastName": "Smith",
      "email": "student@example.com",
      "userType": "STUDENT"
    },
    "studentId": "student_uuid",
    "academicYearGroupResponseDTO": {
      "academicYearGroupId": "group_uuid",
      "academicYear": {
        "academicYearId": "year_uuid",
        "yearName": "2024-2025"
      },
      "group": {
        "groupId": "group_uuid",
        "groupName": "Computer Science Group A"
      }
    }
  }
}
```

### For Viewer Users:
```json
{
  "token": "jwt_token_here",
  "refreshToken": "refresh_token_here",
  "userType": "VIEWER",
  "email": "viewer@example.com",
  "fullName": "Viewer User"
}
```

## Usage Examples

### In AuthenticationService:
```java
// The service automatically handles user type and includes appropriate data
public AuthenticationResponse authenticate(AuthenticationRequest request) {
    // ... authentication logic ...
    
    switch (userDetails.getUserType().toUpperCase()) {
        case "ADMIN":
            AdminResponseDTO adminData = adminService.getAdminByUserId(userId);
            return AuthenticationResponseBuilder.buildAdminResponse(
                    jwtToken, refreshToken, email, fullName, adminData);
        
        case "STUDENT":
            StudentResponseDTO studentData = studentService.getStudentByUserId(userId);
            return AuthenticationResponseBuilder.buildStudentResponse(
                    jwtToken, refreshToken, email, fullName, studentData);
        
        case "VIEWER":
            return AuthenticationResponseBuilder.buildViewerResponse(
                    jwtToken, refreshToken, email, fullName);
    }
}
```

### Client-Side Usage:
```javascript
// Example of how to handle the response on the client side
const response = await fetch('/api/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
});

const authData = await response.json();

// Store tokens
localStorage.setItem('token', authData.token);
localStorage.setItem('refreshToken', authData.refreshToken);

// Handle user-specific data
if (authData.userData) {
    switch (authData.userData.type) {
        case 'ADMIN':
            // Handle admin-specific data
            console.log('Admin ID:', authData.userData.adminId);
            console.log('Specialization:', authData.userData.specializationResponseDTO.specializationName);
            break;
            
        case 'STUDENT':
            // Handle student-specific data
            console.log('Student ID:', authData.userData.studentId);
            console.log('Academic Year:', authData.userData.academicYearGroupResponseDTO.academicYear.yearName);
            break;
    }
}
```

## Helper Methods

### AuthenticationResponse Methods:
```java
// Check if user data is present
boolean hasUserData = response.hasUserData();

// Get user data type
String dataType = response.getUserDataType(); // "ADMIN", "STUDENT", or null

// Type-safe data access
AdminResponseDTO adminData = response.getAdminData();
StudentResponseDTO studentData = response.getStudentData();

// Set user data
response.setAdminData(adminResponseDTO);
response.setStudentData(studentResponseDTO);
```

## Benefits

1. **Single Endpoint**: One login endpoint returns all necessary user information
2. **Type Safety**: Proper typing with Jackson annotations
3. **Flexibility**: Easy to extend for new user types
4. **Performance**: Reduces multiple API calls after login
5. **Maintainability**: Clean, organized code structure
6. **Error Handling**: Graceful fallback if user-specific data cannot be fetched

## Error Handling

The design includes robust error handling:
- If user-specific data cannot be fetched, the response still includes basic authentication info
- Logs errors for debugging
- Continues authentication flow even if additional data fails

## Future Extensions

The design is easily extensible:
- Add new user types by extending the switch statement
- Add new response DTOs by updating Jackson annotations
- Add new utility builder methods for new user types 