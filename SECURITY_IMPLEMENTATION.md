# Spring JWT Security Implementation

## Overview

This document describes the implementation of Spring JWT Security for the Examination Management System with role-based access control.

## User Types

The system supports 4 user types with different access levels:

1. **SuperAdmin** - Full access to all endpoints
2. **Admin** - Access to most administrative functions
3. **Viewer** - Read-only access to most resources
4. **Student** - Limited access to exams and student-specific functions

## Security Architecture

### Components

1. **JwtAuthenticationFilter** - Filters incoming requests and validates JWT tokens
2. **JwtService** - Handles JWT token generation, validation, and extraction
3. **CustomUserDetails** - Custom UserDetails implementation with user type
4. **CustomUserDetailsService** - Loads user details from database
5. **AuthenticationService** - Handles login and registration
6. **SecurityConfig** - Main security configuration with role-based access

### Authentication Flow

1. User sends credentials to `/api/v1/auth/login`
2. System validates credentials and returns JWT token
3. Client includes JWT token in Authorization header: `Bearer <token>`
4. JwtAuthenticationFilter validates token and sets authentication context
5. Spring Security checks role-based permissions

## API Endpoints

### Authentication Endpoints (Public)

- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login user

### Role-Based Access Control

#### SuperAdmin Access
- All endpoints accessible
- Full CRUD operations on all resources

#### Admin Access
- User management (create, read, update)
- Exam management (create, read, update, delete)
- Academic year management
- Course management
- Resource management

#### Viewer Access
- Read-only access to:
  - Exams (GET only)
  - Students (GET only)
  - Admins (GET only)
  - Academic years (GET only)
  - Courses (GET only)
  - Groups (GET only)
  - Resources (GET only)

#### Student Access
- Limited access to:
  - View exams (GET only)
  - Take exams (POST student-exams)
  - View exam results
  - View available languages

## Implementation Details

### JWT Configuration

```yaml
application:
  security:
    jwt:
      secret-key: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
      expiration: 86400000 # 24 hours
      refresh-token:
        expiration: 604800000 # 7 days
```

### User Entity

The User entity includes a `userType` field that determines the user's role:

```java
@Column(name = "user_type", nullable = false, length = 50)
private String userType;
```

### Role Mapping

User types are mapped to Spring Security roles:
- `SUPERADMIN` → `ROLE_SUPERADMIN`
- `ADMIN` → `ROLE_ADMIN`
- `VIEWER` → `ROLE_VIEWER`
- `STUDENT` → `ROLE_STUDENT`

### Method-Level Security

Controllers use `@PreAuthorize` annotations for fine-grained access control:

```java
@PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
@PostMapping
public ResponseEntity<ExamResponseDTO> saveExam(@Valid @RequestBody ExamRequestDTO request) {
    // Only SuperAdmin and Admin can create exams
}
```

## Usage Examples

### Register a User

```bash
curl -X POST http://localhost:8083/ems/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "userType": "ADMIN"
  }'
```

### Login

```bash
curl -X POST http://localhost:8083/ems/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

### Access Protected Endpoint

```bash
curl -X GET http://localhost:8083/ems/api/v1/exams \
  -H "Authorization: Bearer <your-jwt-token>"
```

## Test Endpoints

The system includes test endpoints to verify role-based access:

- `GET /api/v1/test/superadmin` - SuperAdmin only
- `GET /api/v1/test/admin` - Admin and SuperAdmin
- `GET /api/v1/test/viewer` - Viewer, Admin, and SuperAdmin
- `GET /api/v1/test/student` - All authenticated users
- `GET /api/v1/test/public` - No authentication required

## Security Features

1. **JWT Token Authentication** - Stateless authentication
2. **Role-Based Access Control** - Fine-grained permissions
3. **Password Encryption** - BCrypt password hashing
4. **CORS Configuration** - Cross-origin resource sharing
5. **Method-Level Security** - Annotation-based access control
6. **Token Expiration** - Configurable token lifetime
7. **Refresh Tokens** - Long-term authentication support

## Dependencies

The implementation uses the following dependencies:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
</dependency>
```

## Best Practices

1. **Never store sensitive data in JWT tokens**
2. **Use HTTPS in production**
3. **Implement token refresh mechanism**
4. **Log security events**
5. **Regular security audits**
6. **Keep dependencies updated**

## Troubleshooting

### Common Issues

1. **403 Forbidden** - User doesn't have required role
2. **401 Unauthorized** - Invalid or missing JWT token
3. **Token Expired** - JWT token has expired, need to login again

### Debug Mode

Enable debug logging for security:

```yaml
logging:
  level:
    org.springframework.security: DEBUG
    com.university.exam.security: DEBUG
``` 