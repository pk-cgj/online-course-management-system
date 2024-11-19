package com.coursemanagement.service;

import com.coursemanagement.config.JwtTokenExtractor;
import com.coursemanagement.dto.CourseDTO;
import com.coursemanagement.entity.Category;
import com.coursemanagement.entity.Course;
import com.coursemanagement.entity.User;
import com.coursemanagement.repository.CategoryRepository;
import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void createOrUpdateUserFromKeycloak(JwtTokenExtractor.TokenInfo tokenInfo) {
        userRepository.findByEmail(tokenInfo.email())
                .ifPresentOrElse(
                        user -> updateExistingUser(user, tokenInfo),
                        () -> createNewUser(tokenInfo)
                );
    }

    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        User instructor = userRepository.findById(courseDTO.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Category category = categoryRepository.findById(courseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Course course = Course.builder()
                .title(courseDTO.getTitle())
                .description(courseDTO.getDescription())
                .instructor(instructor)
                .category(category)
                .difficultyLevel(courseDTO.getDifficultyLevel())
                .durationHours(courseDTO.getDurationHours())
                .isPublished(courseDTO.getIsPublished())
                .build();

        courseRepository.save(course);
        return mapToCourseDto(course);
    }

    @Transactional
    public CourseDTO editCourse(Long courseId, CourseDTO courseDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Category category = categoryRepository.findById(courseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setCategory(category);
        course.setDifficultyLevel(courseDTO.getDifficultyLevel());
        course.setDurationHours(courseDTO.getDurationHours());
        course.setIsPublished(courseDTO.getIsPublished());

        courseRepository.save(course);
        return mapToCourseDto(course);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        courseRepository.delete(course);
    }

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToCourseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void enrollInCourse(Long courseId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        student.getEnrolledCourses().add(course);
        userRepository.save(student);
    }

    public List<CourseDTO> getEnrolledCourses(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return student.getEnrolledCourses().stream()
                .map(this::mapToCourseDto)
                .collect(Collectors.toList());
    }

    private void updateExistingUser(User user, JwtTokenExtractor.TokenInfo tokenInfo) {
        if (!user.getRole().equals(tokenInfo.role())) {
            log.info("Updating role for user with email: {}", tokenInfo.email());
            user.setRole(tokenInfo.role());
            user.setUpdatedAt(ZonedDateTime.now());
            userRepository.save(user);
        }
    }

    private void createNewUser(JwtTokenExtractor.TokenInfo tokenInfo) {
        log.info("Creating new user with email: {}", tokenInfo.email());

        String[] nameParts = splitFullName(tokenInfo.name());
        String firstName = nameParts[0];
        String lastName = nameParts[1];

        User newUser = User.builder()
                .email(tokenInfo.email())
                .firstName(firstName)
                .lastName(lastName)
                .role(tokenInfo.role())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        userRepository.save(newUser);
    }

    private String[] splitFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new String[]{"Unknown", ""};
        }

        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        return new String[]{firstName, lastName};
    }

    private CourseDTO mapToCourseDto(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructorId(course.getInstructor().getId())
                .categoryId(course.getCategory().getId())
                .difficultyLevel(course.getDifficultyLevel())
                .durationHours(course.getDurationHours())
                .isPublished(course.getIsPublished())
                .build();
    }
}
