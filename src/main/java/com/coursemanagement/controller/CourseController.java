package com.coursemanagement.controller;

import com.coursemanagement.dto.CourseDto;
import com.coursemanagement.dto.CourseSimpleDto;
import com.coursemanagement.entity.Course;
import com.coursemanagement.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Course Management Controller", description = "APIs for managing courses")
public class CourseController {
    private final CourseService courseService;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping
    @Operation(summary = "Create a course", tags = {"Instructor"})
    public ResponseEntity<CourseDto> createCourse(
            @Valid @RequestBody CourseDto.CreateCourseDto createCourseDto,
            @AuthenticationPrincipal Jwt jwt) {
        String instructorEmail = jwt.getClaimAsString("email");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(createCourseDto, instructorEmail));
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseAccessValidator.isInstructor(#courseId, authentication)")
    @PutMapping("/{courseId}")
    @Operation(summary = "Update a course", tags = {"Instructor"})
    public ResponseEntity<CourseDto> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseDto.UpdateCourseDto updateCourseDto) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, updateCourseDto));
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseAccessValidator.isInstructor(#courseId, authentication)")
    @PutMapping("/{courseId}/publish")
    @Operation(summary = "Publish a course", tags = {"Instructor"})
    public ResponseEntity<CourseDto> publishCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.publishCourse(courseId));
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseAccessValidator.isInstructor(#courseId, authentication)")
    @DeleteMapping("/{courseId}")
    @Operation(summary = "Delete a course", tags = {"Instructor"})
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/instructor")
    @Operation(summary = "Get created courses", tags = {"Instructor"})
    public ResponseEntity<List<CourseDto>> getInstructorCourses(@AuthenticationPrincipal Jwt jwt) {
        String instructorEmail = jwt.getClaimAsString("email");
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorEmail));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{courseId}/enroll")
    @Operation(summary = "Enroll in a course", tags = {"Student"})
    public ResponseEntity<Void> enrollInCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal Jwt jwt) {
        String studentEmail = jwt.getClaimAsString("email");
        courseService.enrollInCourse(courseId, studentEmail);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{courseId}/enroll")
    @Operation(summary = "Unenroll from a course", tags = {"Student"})
    public ResponseEntity<Void> unenrollFromCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal Jwt jwt) {
        String studentEmail = jwt.getClaimAsString("email");
        courseService.unEnrollFromCourse(courseId, studentEmail);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student")
    @Operation(summary = "Get enrolled courses", tags = {"Student"})
    public ResponseEntity<List<CourseSimpleDto>> getEnrolledCourses(@AuthenticationPrincipal Jwt jwt) {
        String studentEmail = jwt.getClaimAsString("email");
        return ResponseEntity.ok(courseService.getEnrolledCourses(studentEmail));
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Get a course", tags = {"Public"})
    public ResponseEntity<CourseDto> getCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourse(courseId));
    }

    @GetMapping
    @Operation(summary = "Get all courses", tags = {"Public"})
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/search")
    @Operation(summary = "Search courses", tags = {"Public"})
    public ResponseEntity<List<CourseDto>> searchCourses(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String instructor
    ) {
        return ResponseEntity.ok(courseService.searchCourses(title, category, instructor));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter courses", tags = {"Public"})
    public ResponseEntity<List<CourseDto>> filterCourses(
            @RequestParam(required = false) Course.DifficultyLevel difficultyLevel,
            @RequestParam(required = false) Integer duration
    ) {
        return ResponseEntity.ok(courseService.filterCourses(difficultyLevel, duration));
    }
}
