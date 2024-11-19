package com.coursemanagement.repository;

import com.coursemanagement.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    Optional<Lesson> findByIdAndCourseId(Long id, Long courseId);

    List<Lesson> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    @Query("SELECT COALESCE(MAX(l.orderIndex), 0) FROM Lesson l WHERE l.course.id = :courseId")
    Optional<Integer> findMaxOrderIndexByCourseId(@Param("courseId") Long courseId);

    @Modifying
    @Query("UPDATE Lesson l SET l.orderIndex = l.orderIndex - 1 " +
            "WHERE l.course.id = :courseId AND l.orderIndex > :orderIndex")
    void decrementOrderIndexAfter(@Param("orderIndex") Integer orderIndex, @Param("courseId") Long courseId);

    @Modifying
    @Query("UPDATE Lesson l SET l.orderIndex = l.orderIndex - 1 " +
            "WHERE l.course.id = :courseId AND l.orderIndex > :startOrder AND l.orderIndex <= :endOrder")
    void decrementOrderIndexBetween(@Param("startOrder") Integer startOrder,
                                    @Param("endOrder") Integer endOrder,
                                    @Param("courseId") Long courseId);

    @Modifying
    @Query("UPDATE Lesson l SET l.orderIndex = l.orderIndex + 1 " +
            "WHERE l.course.id = :courseId AND l.orderIndex >= :startOrder AND l.orderIndex <= :endOrder")
    void incrementOrderIndexBetween(@Param("startOrder") Integer startOrder,
                                    @Param("endOrder") Integer endOrder,
                                    @Param("courseId") Long courseId);
}
