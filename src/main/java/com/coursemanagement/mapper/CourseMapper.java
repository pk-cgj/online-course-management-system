package com.coursemanagement.mapper;

import com.coursemanagement.dto.CourseDto;
import com.coursemanagement.dto.CourseSimpleDto;
import com.coursemanagement.entity.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CourseMapper {
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final LessonMapper lessonMapper;

    public CourseDto toDto(Course entity) {
        if (entity == null) return null;

        return CourseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .instructor(userMapper.toSimpleDto(entity.getInstructor()))
                .category(categoryMapper.toDto(entity.getCategory()))
                .difficultyLevel(entity.getDifficultyLevel())
                .durationHours(entity.getDurationHours())
                .isPublished(entity.getIsPublished())
                .lessons(entity.getLessons().stream()
                        .map(lessonMapper::toDto)
                        .collect(Collectors.toSet()))
                .enrolledStudents(entity.getEnrolledStudents().stream()
                        .map(userMapper::toSimpleDto)
                        .collect(Collectors.toSet()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public CourseSimpleDto toSimpleDto(Course entity) {
        if (entity == null) return null;

        return CourseSimpleDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .difficultyLevel(entity.getDifficultyLevel())
                .isPublished(entity.getIsPublished())
                .build();
    }

    public Course toEntity(CourseDto.CreateCourseDto dto) {
        if (dto == null) return null;

        return Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .difficultyLevel(dto.getDifficultyLevel())
                .durationHours(dto.getDurationHours())
                .isPublished(false)
                .lessons(new HashSet<>())
                .enrolledStudents(new HashSet<>())
                .build();
    }

    public void updateEntity(Course entity, CourseDto.UpdateCourseDto dto) {
        if (entity == null || dto == null) return;

        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getDifficultyLevel() != null) entity.setDifficultyLevel(dto.getDifficultyLevel());
        if (dto.getDurationHours() != null) entity.setDurationHours(dto.getDurationHours());
        if (dto.getIsPublished() != null) entity.setIsPublished(dto.getIsPublished());
    }
}
