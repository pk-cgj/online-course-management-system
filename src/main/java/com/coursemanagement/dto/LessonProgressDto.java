package com.coursemanagement.dto;

import com.coursemanagement.entity.LessonProgress;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class LessonProgressDto {
    private Long userId;
    private Long lessonId;
    private LessonSimpleDto lesson;
    private LessonProgress.ProgressStatus status;
    private ZonedDateTime completedAt;
    private ZonedDateTime lastAccessedAt;
    private ZonedDateTime createdAt;

    @Data
    @Builder
    public static class LessonSimpleDto {
        private Long id;
        private String title;
        private Integer orderIndex;
        private Long courseId;
    }

    @Data
    @Builder
    public static class UpdateLessonProgressDto {
        private LessonProgress.ProgressStatus status;
        private ZonedDateTime completedAt;
        private ZonedDateTime lastAccessedAt;
    }
}
