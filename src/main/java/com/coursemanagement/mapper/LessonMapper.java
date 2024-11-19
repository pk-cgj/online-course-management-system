package com.coursemanagement.mapper;

import com.coursemanagement.dto.LessonDto;
import com.coursemanagement.entity.Lesson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LessonMapper {

    public LessonDto toDto(Lesson entity) {
        if (entity == null) return null;

        return LessonDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .content(entity.getContent())
                .orderIndex(entity.getOrderIndex())
                .durationMinutes(entity.getDurationMinutes())
                .isPublished(entity.getIsPublished())
                .courseId(entity.getCourse().getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Lesson toEntity(LessonDto.CreateLessonDto dto) {
        if (dto == null) return null;

        return Lesson.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .content(dto.getContent())
                .orderIndex(dto.getOrderIndex())
                .durationMinutes(dto.getDurationMinutes())
                .isPublished(false)
                .build();
    }

    public void updateEntity(Lesson entity, LessonDto.UpdateLessonDto dto) {
        if (entity == null || dto == null) return;

        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getContent() != null) entity.setContent(dto.getContent());
        if (dto.getOrderIndex() != null) entity.setOrderIndex(dto.getOrderIndex());
        if (dto.getDurationMinutes() != null) entity.setDurationMinutes(dto.getDurationMinutes());
        if (dto.getIsPublished() != null) entity.setIsPublished(dto.getIsPublished());
    }
}
