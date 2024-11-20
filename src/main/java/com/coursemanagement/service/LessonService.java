package com.coursemanagement.service;

import com.coursemanagement.dto.LessonDto;
import com.coursemanagement.entity.Course;
import com.coursemanagement.entity.Lesson;
import com.coursemanagement.exception.CourseEntityNotFoundException;
import com.coursemanagement.mapper.LessonMapper;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.LessonProgressRepository;
import com.coursemanagement.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonMapper lessonMapper;

    @Transactional
    public LessonDto createLesson(LessonDto.CreateLessonDto createLessonDto) {
        Course course = courseRepository.findById(createLessonDto.getCourseId())
                .orElseThrow(() -> new CourseEntityNotFoundException("Course not found"));

        // Determine the order index
        Integer maxOrderIndex = lessonRepository.findMaxOrderIndexByCourseId(course.getId())
                .orElse(0);

        Lesson lesson = Lesson.builder()
                .title(createLessonDto.getTitle())
                .description(createLessonDto.getDescription())
                .content(createLessonDto.getContent())
                .course(course)
                .orderIndex(maxOrderIndex + 1)
                .durationMinutes(createLessonDto.getDurationMinutes())
                .isPublished(false)
                .build();

        lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    @Transactional
    public LessonDto updateLesson(Long lessonId, LessonDto.UpdateLessonDto updateLessonDto) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CourseEntityNotFoundException("Lesson not found"));

        if (updateLessonDto.getOrderIndex() != null &&
                !updateLessonDto.getOrderIndex().equals(lesson.getOrderIndex())) {
            handleLessonReordering(lesson, updateLessonDto.getOrderIndex());
        }

        lessonMapper.updateEntity(lesson, updateLessonDto);
        lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    @Transactional
    public LessonDto publishLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CourseEntityNotFoundException("Lesson not found"));

        lesson.setIsPublished(true);
        lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    @Transactional
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CourseEntityNotFoundException("Lesson not found"));

        lessonRepository.decrementOrderIndexAfter(lesson.getOrderIndex(), lesson.getCourse().getId());

        lessonRepository.delete(lesson);
    }

    public List<LessonDto> getLessonsByCourse(Long courseId) {
        return lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                .map(lessonMapper::toDto)
                .collect(Collectors.toList());
    }

    private void handleLessonReordering(Lesson lesson, Integer newOrderIndex) {
        Long courseId = lesson.getCourse().getId();
        Integer currentOrderIndex = lesson.getOrderIndex();

        if (newOrderIndex > currentOrderIndex) {
            // Move other lessons down
            lessonRepository.decrementOrderIndexBetween(currentOrderIndex, newOrderIndex, courseId);
        } else {
            // Move other lessons up
            lessonRepository.incrementOrderIndexBetween(newOrderIndex, currentOrderIndex, courseId);
        }

        lesson.setOrderIndex(newOrderIndex);
    }
}
