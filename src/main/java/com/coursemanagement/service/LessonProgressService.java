package com.coursemanagement.service;

import com.coursemanagement.dto.LessonProgressDto;
import com.coursemanagement.entity.Course;
import com.coursemanagement.entity.Lesson;
import com.coursemanagement.entity.LessonProgress;
import com.coursemanagement.entity.User;
import com.coursemanagement.exception.CourseEntityNotFoundException;
import com.coursemanagement.mapper.LessonProgressMapper;
import com.coursemanagement.repository.LessonProgressRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonProgressService {
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    private final LessonProgressMapper lessonProgressMapper;


    @Transactional
    public void initializeLessonProgressForCourse(User student, Course course) {
        List<Lesson> courseLessons = lessonRepository.findByCourseIdOrderByOrderIndexAsc(course.getId());

        List<LessonProgress> lessonProgresses = courseLessons.stream()
                .map(lesson -> LessonProgress.builder()
                        .userId(student.getId())
                        .lessonId(lesson.getId())
                        .lesson(lesson)
                        .status(LessonProgress.ProgressStatus.NOT_STARTED)
                        .build())
                .collect(Collectors.toList());

        lessonProgressRepository.saveAll(lessonProgresses);
    }

    @Transactional
    public LessonProgressDto updateLessonProgress(String studentEmail, Long lessonId,
                                                  LessonProgressDto.UpdateLessonProgressDto updateDto) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new CourseEntityNotFoundException("Student not found"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CourseEntityNotFoundException("Lesson not found"));

        if (!lesson.getIsPublished()) {
            throw new IllegalStateException("Cannot update progress in an unpublished lesson");
        }

        LessonProgress lessonProgress = lessonProgressRepository
                .findByUserIdAndLessonId(student.getId(), lessonId)
                .orElse(LessonProgress.builder()
                        .userId(student.getId())
                        .lessonId(lessonId)
                        .lesson(lesson)
                        .status(LessonProgress.ProgressStatus.NOT_STARTED)
                        .build());

        lessonProgress.setStatus(updateDto.getStatus());
        lessonProgress.setCompletedAt(updateDto.getCompletedAt());
        lessonProgress.setLastAccessedAt(updateDto.getLastAccessedAt());

        lessonProgressRepository.save(lessonProgress);
        return lessonProgressMapper.toDto(lessonProgress);
    }

    @Transactional
    public LessonProgressDto updateLessonProgressStatus(String studentEmail, Long lessonId, LessonProgress.ProgressStatus status) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new CourseEntityNotFoundException("Student not found"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CourseEntityNotFoundException("Lesson not found"));

        if (!lesson.getIsPublished()) {
            throw new IllegalStateException("Cannot update progress in an unpublished lesson");
        }

        LessonProgress lessonProgress = lessonProgressRepository
                .findByUserIdAndLessonId(student.getId(), lessonId)
                .orElseThrow(() -> new CourseEntityNotFoundException("Lesson Progress Report not found"));


        lessonProgress.setStatus(status);

        lessonProgressRepository.save(lessonProgress);
        return lessonProgressMapper.toDto(lessonProgress);
    }

    public List<LessonProgressDto> getLessonProgressByCourse(String studentEmail, Long courseId) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new CourseEntityNotFoundException("Student not found"));

        return lessonProgressRepository.findByUserIdAndCourseId(student.getId(), courseId).stream()
                .map(lessonProgressMapper::toDto)
                .collect(Collectors.toList());
    }
}
