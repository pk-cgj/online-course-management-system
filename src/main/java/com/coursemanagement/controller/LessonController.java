package com.coursemanagement.controller;

import com.coursemanagement.dto.LessonDto;
import com.coursemanagement.dto.LessonProgressDto;
import com.coursemanagement.entity.LessonProgress;
import com.coursemanagement.service.LessonProgressService;
import com.coursemanagement.service.LessonService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Lesson Management Controller", description = "APIs for managing lessons and lesson progress")
public class LessonController {
    private final LessonService lessonService;
    private final LessonProgressService lessonProgressService;

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseAccessValidator.isInstructor(#courseId, authentication)")
    @PostMapping("/courses/{courseId}/lessons")
    @Operation(summary = "Create a lesson for a course", tags = {"Instructor"})
    public ResponseEntity<LessonDto> createLesson(
            @PathVariable Long courseId,
            @Valid @RequestBody LessonDto.CreateLessonDto createLessonDto) {
        createLessonDto.setCourseId(courseId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lessonService.createLesson(createLessonDto));
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseAccessValidator.isInstructor(#courseId, authentication)")
    @PutMapping("/courses/{courseId}/lessons/{lessonId}")
    @Operation(summary = "Update a lesson", tags = {"Instructor"})
    public ResponseEntity<LessonDto> updateLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonDto.UpdateLessonDto updateLessonDto) {
        return ResponseEntity.ok(lessonService.updateLesson(lessonId, updateLessonDto));
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseAccessValidator.isInstructor(#courseId, authentication)")
    @PutMapping("/courses/{courseId}/lessons/{lessonId}/publish")
    @Operation(summary = "Publish a lesson", tags = {"Instructor"})
    public ResponseEntity<LessonDto> publishLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {
        return ResponseEntity.ok(lessonService.publishLesson(lessonId));
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseAccessValidator.isInstructor(#courseId, authentication)")
    @DeleteMapping("/courses/{courseId}/lessons/{lessonId}")
    @Operation(summary = "Delete a lesson", tags = {"Instructor"})
    public ResponseEntity<Void> deleteLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/lessons/{lessonId}/progress")
    @Operation(summary = "Update lesson progress", tags = {"Student"})
    public ResponseEntity<LessonProgressDto> updateLessonProgress(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonProgressDto.UpdateLessonProgressDto updateDto,
            @AuthenticationPrincipal Jwt jwt) {
        String studentEmail = jwt.getClaimAsString("email");
        return ResponseEntity.ok(lessonProgressService.updateLessonProgress(studentEmail, lessonId, updateDto));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/lessons/{lessonId}/progress")
    @Operation(summary = "Update lesson progress status", tags = {"Student"})
    public ResponseEntity<LessonProgressDto> updateLessonProgressStatus(
            @PathVariable Long lessonId,
            @RequestParam LessonProgress.ProgressStatus status,
            @AuthenticationPrincipal Jwt jwt) {
        String studentEmail = jwt.getClaimAsString("email");
        return ResponseEntity.ok(lessonProgressService.updateLessonProgressStatus(studentEmail, lessonId, status));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/courses/{courseId}/progress")
    @Operation(summary = "Get lesson progress for a course", tags = {"Student"})
    public ResponseEntity<List<LessonProgressDto>> getLessonProgressByCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal Jwt jwt) {
        String studentEmail = jwt.getClaimAsString("email");
        return ResponseEntity.ok(lessonProgressService.getLessonProgressByCourse(studentEmail, courseId));
    }

    @GetMapping("/courses/{courseId}/lessons")
    @Operation(summary = "Get lessons for a course", tags = {"Public"})
    public ResponseEntity<List<LessonDto>> getLessonsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId));
    }
}
