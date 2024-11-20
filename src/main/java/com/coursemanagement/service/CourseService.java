package com.coursemanagement.service;

import com.coursemanagement.dto.CourseDto;
import com.coursemanagement.dto.CourseSimpleDto;
import com.coursemanagement.entity.Category;
import com.coursemanagement.entity.Course;
import com.coursemanagement.entity.LessonProgress;
import com.coursemanagement.entity.User;
import com.coursemanagement.exception.CourseEntityNotFoundException;
import com.coursemanagement.mapper.CourseMapper;
import com.coursemanagement.repository.CategoryRepository;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.LessonProgressRepository;
import com.coursemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonProgressService lessonProgressService;
    private final CourseMapper courseMapper;

    @Transactional
    public CourseDto createCourse(CourseDto.CreateCourseDto createCourseDto, String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new CourseEntityNotFoundException("Instructor not found"));

        Category category = categoryRepository.findById(createCourseDto.getCategoryId())
                .orElseThrow(() -> new CourseEntityNotFoundException("Category not found"));

        Course course = courseMapper.toEntity(createCourseDto);
        course.setInstructor(instructor);
        course.setCategory(category);
        course.setIsPublished(false);

        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    @Transactional
    public CourseDto updateCourse(Long courseId, CourseDto.UpdateCourseDto updateCourseDto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseEntityNotFoundException("Course not found"));

        if (updateCourseDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateCourseDto.getCategoryId())
                    .orElseThrow(() -> new CourseEntityNotFoundException("Category not found"));
            course.setCategory(category);
        }

        courseMapper.updateEntity(course, updateCourseDto);
        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    @Transactional
    public CourseDto publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseEntityNotFoundException("Course not found"));

        course.setIsPublished(true);
        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseEntityNotFoundException("Course not found"));
        courseRepository.delete(course);
    }

    public CourseDto getCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .map(courseMapper::toDto)
                .orElseThrow(() -> new CourseEntityNotFoundException("Course not found"));
    }

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CourseDto> getCoursesByInstructor(String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new CourseEntityNotFoundException("Instructor not found"));

        return courseRepository.findByInstructor(instructor).stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void enrollInCourse(Long courseId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new CourseEntityNotFoundException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseEntityNotFoundException("Course not found"));

        if (!course.getIsPublished()) {
            throw new IllegalStateException("Cannot enroll in an unpublished course");
        }
        course.getLessons()
                .forEach(lesson -> {
                    if (!lesson.getIsPublished()) {
                        throw new IllegalStateException("Cannot enroll in a course with unpublished lesson");
                    }
                });

        student.enrollInCourse(course);
        userRepository.save(student);

        lessonProgressService.initializeLessonProgressForCourse(student, course);
    }

    @Transactional
    public void unEnrollFromCourse(Long courseId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new CourseEntityNotFoundException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseEntityNotFoundException("Course not found"));

        student.unenrollFromCourse(course);
        userRepository.save(student);
    }

    public List<CourseSimpleDto> getEnrolledCourses(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new CourseEntityNotFoundException("Student not found"));

        return student.getEnrolledCourses().stream()
                .map(course -> {
                    CourseSimpleDto courseDto = courseMapper.toSimpleDto(course);

                    List<LessonProgress> progressList = lessonProgressRepository
                            .findByUserIdAndCourseId(student.getId(), course.getId());

                    int totalLessons = progressList.size();
                    int completedLessons = (int) progressList.stream()
                            .filter(lp -> lp.getStatus() == LessonProgress.ProgressStatus.COMPLETED)
                            .count();

                    double progressPercentage = totalLessons > 0
                            ? (completedLessons * 100.0 / totalLessons)
                            : 0.0;

                    courseDto.setProgress(CourseSimpleDto.CourseProgressDto.builder()
                            .totalLessons(totalLessons)
                            .completedLessons(completedLessons)
                            .progressPercentage(progressPercentage)
                            .build());

                    return courseDto;
                })
                .collect(Collectors.toList());
    }

    public List<CourseDto> searchCourses(String title, String category, String instructor) {
        String firstName = null;
        String lastName = null;

        if (instructor != null) {
            String[] parts = instructor.split(" ");
            firstName = parts[0];
            lastName = parts[1];
        }
        List<Course> courses = courseRepository.searchCourses(title, category, firstName, lastName);
        return courses.stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CourseDto> filterCourses(Course.DifficultyLevel difficultyLevel, Integer duration) {
        List<Course> courses = courseRepository.filterCourses(difficultyLevel, duration);
        return courses.stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }
}
