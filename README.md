# Online Course Management System

This project is a **Spring Boot** application for managing online courses. It allows instructors to create and manage
courses while enabling students to browse, enroll, and access course materials. The system implements user roles, secure
authentication, and basic course management features.

## Features

1. **User Roles and Authentication**
    - Authentication and Authorization via **Keycloak** and **Spring Security**.
    - **JWT Tokens** are used for secure API access.
    - Roles:
        - **Instructor**: Create and manage courses.
        - **Student**: Enroll, browse, and interact with courses.

2. **Course Management (Instructor Role)**
    - Instructors can:
        - Create and edit courses, including title, description, and category.
        - Add lessons/modules to courses.
        - Delete courses.

3. **Enrollment and Access (Student Role)**
    - Students can:
        - Browse and search available courses.
        - Enroll in courses.
        - Access reading materials and course content.

4. **Database Design**
    - **H2 Database** (in-memory) is used for testing and development.
    - Models include:
        - Users (with roles: Instructor/Student).
        - Courses (title, description, category).
        - Lessons (linked to courses).
        - Enrollments (tracks student-course relationships).

## Setup

### Prerequisites

- **Java 17** or higher
- **Docker** and **Docker Compose**

### Running the Application

1. **Clone the Repository**
    ```sh
    git clone https://github.com/pk-cgj/online-course-management-system.git
    cd online-course-management-system
    ```

2. **Start Services with Docker Compose**
    ```sh
    docker-compose up
    ```

3. **Access the Application**
    - Keycloak: `http://localhost:8888`
    - Application: `http://localhost:8080/swagger-ui.html` (Swagger UI for API documentation)
