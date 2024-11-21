# Online Course Management System

## Project Overview

A comprehensive Spring Boot-based Online Course Management System that provides a platform for instructors to create and
manage courses, and students to enroll and track their learning progress.

## Technologies Used

- **Framework**: Spring Boot (Java 17)
- **Database**: PostgreSQL
- **Authentication**: Keycloak OAuth2 with JWT
- **Migration**: Flyway
- **Documentation**: Swagger OpenAPI

### Key Dependencies

- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL Driver
- Keycloak OAuth2
- Flyway
- Swagger OpenAPI

## System Architecture

### Microservices Configuration

- **Keycloak**: Authentication and Authorization Server
- **PostgreSQL**: Primary Database
- **Online Course Application**: Main Application Server

## Authentication and Authorization

### Identity Management

- **Platform**: Keycloak
- **Authentication Flow**: OAuth2 Authorization Code
- **Token Type**: JWT (JSON Web Tokens)

### Predefined Users

| Username | Password | Role        |
|----------|----------|-------------|
| jamesdio | jamesdio | ADMIN       |
| johndoe  | johndoe  | INSTRUCTOR  |
| janedoe  | janedoe  | STUDENT     |

## Features

### User Roles and Permissions

1. **ADMIN**
    - Manage system categories
    - Full administrative access

2. **INSTRUCTOR**
    - Create and manage courses
    - Add course modules and lessons
    - Publish/unpublish courses
    - Track student enrollments

3. **STUDENT**
    - Browse available courses
    - Enroll/unenroll in courses
    - Access course content
    - Track learning progress

### Course Management

- Create courses with detailed information
- Multi-level course structure (Courses → Lessons)
- Course categorization
- Course publishing workflow
- Search and filter capabilities

### Lesson Management

- Create, update, and delete lessons
- Lesson progress tracking
- Progress status management

## API Endpoints

### Authentication Endpoints

- `POST /api/login`: User authentication
- `GET /api/user-info`: Retrieve user information

### Course Endpoints

- `POST /api/v1/courses`: Create a course
- `GET /api/v1/courses`: List all courses
- `GET /api/v1/courses/search`: Search courses
- `GET /api/v1/courses/filter`: Filter courses
- `POST /api/v1/courses/{courseId}/enroll`: Enroll in a course

### Lesson Endpoints

- `POST /api/v1/courses/{courseId}/lessons`: Create a lesson
- `PUT /api/v1/lessons/{lessonId}/progress`: Update lesson progress
- `GET /api/v1/courses/{courseId}/lessons`: Get course lessons

### Category Endpoints

- `POST /api/v1/categories`: Create a category
- `GET /api/v1/categories`: List all categories

## Development Setup

### Prerequisites

- Java 17
- Docker
- PostgreSQL
- Keycloak

### Configuration

1. Clone the repository
2. Set environment variables in `docker-compose.yml`
3. Run Docker Compose:
   ```bash
   docker-compose up -d
   ```

### Local Development

- Provide environment variables for existing `application.properties`
- Configure PostgreSQL connection
- Set up Keycloak realm

## API Documentation

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/v3/api-docs`

## Docker Composition

### Services

- **Keycloak**: Authentication Server
- **PostgreSQL**: Database
- **Online Course App**: Main Application

## Security Considerations

- OAuth2 Resource Server
- JWT Token Validation
- Role-Based Access Control
- Secure Endpoint Protection

## Monitoring and Logging

- Slf4j logging
- Health checks for services

## Future Improvements

- Implement course reviews
- Advanced filtering
- More granular access controls
- Comprehensive error handling

## Troubleshooting

- Verify Keycloak realm configuration
- Check database connections
- Validate JWT token settings