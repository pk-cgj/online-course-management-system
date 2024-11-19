package com.coursemanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private ZonedDateTime createdAt;

    @Data
    @Builder
    public static class CreateCategoryDto {
        private String name;
        private String description;
    }

    @Data
    @Builder
    public static class UpdateCategoryDto {
        private String name;
        private String description;
    }
}
