package com.coursemanagement.mapper;

import com.coursemanagement.dto.CategoryDto;
import com.coursemanagement.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    public CategoryDto toDto(Category entity) {
        if (entity == null) return null;

        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public Category toEntity(CategoryDto.CreateCategoryDto dto) {
        if (dto == null) return null;

        return Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public void updateEntity(Category entity, CategoryDto.UpdateCategoryDto dto) {
        if (entity == null || dto == null) return;

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
    }
}
