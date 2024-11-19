package com.coursemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class LessonProgressId implements java.io.Serializable {
    private Long userId;
    private Long lessonId;
}
