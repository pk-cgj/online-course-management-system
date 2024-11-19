package com.coursemanagement.mapper;

import com.coursemanagement.dto.LessonProgressDto;
import com.coursemanagement.entity.Lesson;
import com.coursemanagement.entity.LessonProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LessonProgressMapper {

    public LessonProgressDto toDto(LessonProgress entity) {
        if (entity == null) return null;

        return LessonProgressDto.builder()
                .userId(entity.getUserId())
                .lessonId(entity.getLesson().getId())  // Get ID from lesson relationship
                .status(entity.getStatus())
                .completedAt(entity.getCompletedAt())
                .lastAccessedAt(entity.getLastAccessedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private LessonProgressDto.LessonSimpleDto toLessonSimpleDto(Lesson lesson) {
        if (lesson == null) return null;

        return LessonProgressDto.LessonSimpleDto.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .orderIndex(lesson.getOrderIndex())
                .courseId(lesson.getCourse().getId())
                .build();
    }

    public LessonProgress toEntity(Long userId, Lesson lesson) {
        if (userId == null || lesson == null) {
            throw new IllegalArgumentException("Both userId and lesson must be provided");
        }

        return LessonProgress.builder()
                .userId(userId)
                .lessonId(lesson.getId())
                .lesson(lesson)
                .status(LessonProgress.ProgressStatus.NOT_STARTED)
                .build();
    }

    public void updateEntity(LessonProgress entity, LessonProgressDto.UpdateLessonProgressDto dto) {
        if (entity == null || dto == null) return;

        Optional.ofNullable(dto.getStatus())
                .ifPresent(entity::setStatus);

        Optional.ofNullable(dto.getCompletedAt())
                .ifPresent(entity::setCompletedAt);

        Optional.ofNullable(dto.getLastAccessedAt())
                .ifPresent(entity::setLastAccessedAt);
    }

    public void updateEntityFromDto(LessonProgressDto.UpdateLessonProgressDto dto, LessonProgress entity) {
        if (dto == null) {
            throw new IllegalArgumentException("UpdateLessonProgressDto cannot be null");
        }
        if (entity == null) {
            throw new IllegalArgumentException("LessonProgress entity cannot be null");
        }

        Optional.ofNullable(dto.getStatus())
                .ifPresent(entity::setStatus);

        Optional.ofNullable(dto.getCompletedAt())
                .ifPresent(entity::setCompletedAt);

        Optional.ofNullable(dto.getLastAccessedAt())
                .ifPresent(entity::setLastAccessedAt);
    }
}
