package ru.practicum.ewm.service.categories;

import ru.practicum.ewm.service.categories.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);

    CategoryDto createCategory(CategoryDto request);

    CategoryDto updateCategory(CategoryDto request);

    void deleteCategory(Long catId);
}
