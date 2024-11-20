package com.coursemanagement.service;

import com.coursemanagement.dto.CategoryDto;
import com.coursemanagement.entity.Category;
import com.coursemanagement.entity.Course;
import com.coursemanagement.exception.CourseEntityNotFoundException;
import com.coursemanagement.mapper.CategoryMapper;
import com.coursemanagement.repository.CategoryRepository;
import com.coursemanagement.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDto createCategory(CategoryDto.CreateCategoryDto createCategoryDto) {
        Category existingCategory = categoryRepository.findByName(createCategoryDto.getName());
        if (existingCategory != null) {
            throw new IllegalArgumentException("Category with this name already exists");
        }

        Category category = categoryMapper.toEntity(createCategoryDto);
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Transactional
    public CategoryDto updateCategory(Long categoryId, CategoryDto.UpdateCategoryDto updateCategoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CourseEntityNotFoundException("Category not found"));

        Category existingCategory = categoryRepository.findByName(updateCategoryDto.getName());
        if (existingCategory != null && !existingCategory.getId().equals(categoryId)) {
            throw new IllegalArgumentException("Category with this name already exists");
        }

        categoryMapper.updateEntity(category, updateCategoryDto);
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CourseEntityNotFoundException("Category not found"));

        List<Course> coursesInCategory = courseRepository.findByCategory(category);
        if (!coursesInCategory.isEmpty()) {
            throw new IllegalStateException("Cannot delete category that has associated courses");
        }

        categoryRepository.delete(category);
    }

    public CategoryDto getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new CourseEntityNotFoundException("Category not found"));
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
}
