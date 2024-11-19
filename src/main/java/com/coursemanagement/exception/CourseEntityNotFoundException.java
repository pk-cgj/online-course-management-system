package com.coursemanagement.exception;

public class CourseEntityNotFoundException extends RuntimeException {
    public CourseEntityNotFoundException(String message) {
        super(message);
    }
}
