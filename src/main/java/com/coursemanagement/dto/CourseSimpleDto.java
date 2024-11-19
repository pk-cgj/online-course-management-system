package com.coursemanagement.dto;

import com.coursemanagement.entity.Course;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseSimpleDto {
    private Long id;
    private String title;
    private Course.DifficultyLevel difficultyLevel;
    private Boolean isPublished;
    private CourseProgressDto progress;

    @Data
    @Builder
    public static class CourseProgressDto {
        private int totalLessons;
        private int completedLessons;
        private double progressPercentage;
    }
}
