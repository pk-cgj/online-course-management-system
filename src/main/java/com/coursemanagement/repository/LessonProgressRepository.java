package com.coursemanagement.repository;

import com.coursemanagement.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    @Query("SELECT lp FROM LessonProgress lp " +
            "JOIN Lesson l ON lp.lessonId = l.id " +
            "WHERE lp.userId = :userId AND l.course.id = :courseId")
    List<LessonProgress> findByUserIdAndCourseId(@Param("userId") Long userId,
                                                 @Param("courseId") Long courseId);
}
