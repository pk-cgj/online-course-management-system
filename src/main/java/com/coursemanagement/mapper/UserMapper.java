package com.coursemanagement.mapper;

import com.coursemanagement.dto.CourseSimpleDto;
import com.coursemanagement.dto.UserDto;
import com.coursemanagement.dto.UserSimpleDto;
import com.coursemanagement.entity.Course;
import com.coursemanagement.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDto toDto(User entity) {
        if (entity == null) return null;

        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .role(entity.getRole())
                .enrolledCourses(entity.getEnrolledCourses().stream()
                        .map(this::toCourseSimpleDto)
                        .collect(Collectors.toSet()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public UserSimpleDto toSimpleDto(User entity) {
        if (entity == null) return null;

        return UserSimpleDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .build();
    }

    public User toEntity(UserDto.CreateUserDto dto) {
        if (dto == null) return null;

        return User.builder()
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .role(dto.getRole())
                .enrolledCourses(new HashSet<>())
                .build();
    }

    public void updateEntity(User entity, UserDto.UpdateUserDto dto) {
        if (entity == null || dto == null) return;

        if (dto.getFirstName() != null) entity.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
        if (dto.getRole() != null) entity.setRole(dto.getRole());
    }

    // Helper method for mapping Course to CourseSimpleDto within UserMapper
    private CourseSimpleDto toCourseSimpleDto(Course course) {
        if (course == null) return null;

        return CourseSimpleDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .difficultyLevel(course.getDifficultyLevel())
                .isPublished(course.getIsPublished())
                .build();
    }
}
