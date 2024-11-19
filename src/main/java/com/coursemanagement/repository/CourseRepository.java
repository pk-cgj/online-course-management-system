package com.coursemanagement.repository;

import com.coursemanagement.entity.Category;
import com.coursemanagement.entity.Course;
import com.coursemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCategory(Category category);

    List<Course> findByInstructor(User instructor);

    @Query(""" 
                SELECT DISTINCT c FROM Course c
                LEFT JOIN c.instructor i
                LEFT JOIN c.category cat
                WHERE
                    (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')))
                    AND (:category IS NULL OR LOWER(cat.name) LIKE LOWER(CONCAT('%', :category, '%')))
                    AND (:firstName IS NULL OR LOWER(i.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')))
                    AND (:lastName IS NULL OR LOWER(i.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))
            """)
    List<Course> searchCourses(
            @Param("title") String title,
            @Param("category") String category,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName
    );

    @Query("""
                SELECT DISTINCT c
                FROM Course c
                LEFT JOIN FETCH c.instructor
                LEFT JOIN FETCH c.category
                WHERE
                    (:difficultyLevel IS NULL OR c.difficultyLevel = :difficultyLevel)
                    AND (:duration IS NULL OR c.durationHours <= :duration)
            """)
    List<Course> filterCourses(
            @Param("difficultyLevel") Course.DifficultyLevel difficultyLevel,
            @Param("duration") Integer duration
    );
}
