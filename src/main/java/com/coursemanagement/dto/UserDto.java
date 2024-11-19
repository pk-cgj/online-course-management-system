package com.coursemanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    @Builder.Default
    private Set<CourseSimpleDto> enrolledCourses = new HashSet<>();
    private ZonedDateTime createdAt;

    @Data
    @Builder
    public static class CreateUserDto {
        private String email;
        private String firstName;
        private String lastName;
        private String role;
    }

    @Data
    @Builder
    public static class UpdateUserDto {
        private String firstName;
        private String lastName;
        private String role;
    }
}
