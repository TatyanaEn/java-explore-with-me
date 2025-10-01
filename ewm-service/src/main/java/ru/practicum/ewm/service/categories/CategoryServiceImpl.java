package ru.practicum.ewm.service.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.categories.dto.CategoryDto;
import ru.practicum.ewm.service.categories.model.Category;
import ru.practicum.ewm.service.exception.ConflictedDataException;
import ru.practicum.ewm.service.exception.NotFoundException;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).map(CategoryMapper::toCategoryDto).getContent();
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(CategoryMapper::toCategoryDto)
                .orElseThrow(() -> new NotFoundException("Категория с ID '%d' не найдена. ".formatted(categoryId), log));
    }

    @Override
    public CategoryDto createCategory(CategoryDto request) {
        if (!categoryRepository.findByName(request.getName()).isEmpty()) {
            throw new ConflictedDataException("Категория с таким именем уже существует", log);
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(request)));
    }

    @Override
    public CategoryDto updateCategory(CategoryDto request) {
        Long categoryId = request.getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с ID '%d' не найдена. ".formatted(categoryId), log));
        if (!(category.getName().equals(request.getName())))
            if (!categoryRepository.findByName(request.getName()).isEmpty()) {
                throw new ConflictedDataException("Категория с таким именем уже существует", log);
            }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.updateCategoryFields(category, request)));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с ID '%d' не найдена. ".formatted(categoryId), log));
        categoryRepository.delete(category);
    }

}
