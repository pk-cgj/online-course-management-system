package com.coursemanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class LessonDto {
    private Long id;
    private String title;
    private String description;
    private String content;
    private Integer orderIndex;
    private Integer durationMinutes;
    private Boolean isPublished;
    private Long courseId;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Data
    @Builder
    public static class CreateLessonDto {
        private String title;
        private String description;
        private String content;
        private Integer orderIndex;
        private Integer durationMinutes;
        private Long courseId;
    }

    @Data
    @Builder
    public static class UpdateLessonDto {
        private String title;
        private String description;
        private String content;
        private Integer orderIndex;
        private Integer durationMinutes;
        private Boolean isPublished;
    }
}
