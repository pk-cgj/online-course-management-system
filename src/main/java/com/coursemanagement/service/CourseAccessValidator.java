package com.coursemanagement.service;

import com.coursemanagement.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseAccessValidator {
    private final CourseRepository courseRepository;

    public boolean isInstructor(Long courseId, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String instructorEmail = jwt.getClaimAsString("email");

        return courseRepository.findById(courseId)
                .map(course -> course.getInstructor().getEmail().equals(instructorEmail))
                .orElse(false);
    }
}
