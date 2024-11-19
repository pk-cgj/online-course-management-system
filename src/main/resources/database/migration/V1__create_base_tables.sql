-- Set the search path to our schema
SET search_path TO course_management;

-- Users table to store both instructors and students
CREATE TABLE users
(
    id          BIGSERIAL PRIMARY KEY,
    keycloak_id VARCHAR(255) NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL UNIQUE,
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    role        VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Categories for organizing courses
CREATE TABLE categories
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Courses table
CREATE TABLE courses
(
    id               BIGSERIAL PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    instructor_id    BIGINT       NOT NULL,
    category_id      BIGINT       NOT NULL,
    difficulty_level VARCHAR(20),
    duration_hours   INT,
    is_published     BOOLEAN                  DEFAULT false,
    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_courses_instructor FOREIGN KEY (instructor_id) REFERENCES users (id),
    CONSTRAINT fk_courses_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE enrollments
(
    user_id   BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, course_id),
    CONSTRAINT fk_enrollments_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE
);

-- Add lessons table
CREATE TABLE lessons (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    content TEXT,
    order_index INT NOT NULL,  -- To maintain lesson sequence within a course
    duration_minutes INT,      -- Estimated time to complete the lesson
    is_published BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lessons_course FOREIGN KEY (course_id)
        REFERENCES courses (id) ON DELETE CASCADE
);

-- Add lesson progress tracking for students
CREATE TABLE lesson_progress (
    user_id BIGINT NOT NULL,
    lesson_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,  -- 'NOT_STARTED', 'IN_PROGRESS', 'COMPLETED'
    completed_at TIMESTAMP WITH TIME ZONE,
    last_accessed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, lesson_id),
    CONSTRAINT fk_lesson_progress_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_lesson_progress_lesson FOREIGN KEY (lesson_id)
        REFERENCES lessons (id) ON DELETE CASCADE
);

-- Add indexes for better performance
CREATE INDEX idx_lessons_course_id ON lessons (course_id);
CREATE INDEX idx_lessons_order ON lessons (course_id, order_index);
CREATE INDEX idx_lesson_progress_user ON lesson_progress (user_id);
CREATE INDEX idx_lesson_progress_lesson ON lesson_progress (lesson_id);
CREATE INDEX idx_lessons_title_gin ON lessons USING gin (to_tsvector('english', title));
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_keycloak_id ON users (keycloak_id);
CREATE INDEX idx_courses_instructor ON courses (instructor_id);
CREATE INDEX idx_courses_category ON courses (category_id);
CREATE INDEX idx_courses_title_gin ON courses USING gin (to_tsvector('english', title));
CREATE INDEX idx_courses_description_gin ON courses USING gin (to_tsvector('english', description));
CREATE INDEX idx_courses_instructor_category ON courses (instructor_id, category_id);
