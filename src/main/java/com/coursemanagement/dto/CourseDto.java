package com.coursemanagement.dto;

import com.coursemanagement.entity.Course;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class CourseDto {
    private Long id;
    private String title;
    private String description;
    private UserSimpleDto instructor;
    private CategoryDto category;
    private Course.DifficultyLevel difficultyLevel;
    private Integer durationHours;
    private Boolean isPublished;
    @Builder.Default
    private Set<LessonDto> lessons = new HashSet<>();
    @Builder.Default
    private Set<UserSimpleDto> enrolledStudents = new HashSet<>();
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Data
    @Builder
    public static class CreateCourseDto {
        private String title;
        private String description;
        private Long categoryId;
        private Course.DifficultyLevel difficultyLevel;
        private Integer durationHours;
    }

    @Data
    @Builder
    public static class UpdateCourseDto {
        private String title;
        private String description;
        private Long categoryId;
        private Course.DifficultyLevel difficultyLevel;
        private Integer durationHours;
        private Boolean isPublished;
    }
}
