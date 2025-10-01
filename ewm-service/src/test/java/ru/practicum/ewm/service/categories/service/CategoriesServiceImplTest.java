package ru.practicum.ewm.service.categories.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.service.categories.CategoryMapper;
import ru.practicum.ewm.service.categories.CategoryRepository;
import ru.practicum.ewm.service.categories.CategoryService;
import ru.practicum.ewm.service.categories.dto.CategoryDto;
import ru.practicum.ewm.service.categories.model.Category;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
class CategoriesServiceImplTest {
    @Autowired
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository categoryRepository;

    private Category category1;

    private Category category2;

    private CategoryDto categoryDto1;

    private CategoryDto categoryDto2;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
        category1 = Category.builder()
                .id(1L)
                .name("Фильмы")
                .build();

        categoryDto1 = CategoryMapper.toCategoryDto(category1);

        MockitoAnnotations.openMocks(this);
        category2 = Category.builder()
                .id(1L)
                .name("Книги")
                .build();

        categoryDto2 = CategoryMapper.toCategoryDto(category2);

    }

    @Test
    void addCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category1);

        CategoryDto categoryDtoTest = categoryService.createCategory(categoryDto1);

        assertEquals(categoryDtoTest.getId(), categoryDto1.getId());
        assertEquals(categoryDtoTest.getName(), categoryDto1.getName());

        verify(categoryRepository, times(1)).save(any(Category.class));
    }


    @Test
    void deleteCategory() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category1));
        categoryService.deleteCategory(1L);
        verify(categoryRepository, times(1)).delete(category1);
    }

    @Test
    void updateCategory() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category1));
        when(categoryRepository.save(any(Category.class))).thenReturn(category1);

        categoryDto1.setName("Мультики");

        CategoryDto categoryDtoUpdated = categoryService.updateCategory(categoryDto1);

        assertEquals(categoryDtoUpdated.getName(), categoryDto1.getName());

        verify(categoryRepository, times(1)).save(category1);
    }

    @Test
    void getCategoryById() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category1));

        CategoryDto categoryDtoTest = categoryService.getCategoryById(1L);

        assertEquals(categoryDtoTest.getId(), categoryDto1.getId());
        assertEquals(categoryDtoTest.getName(), categoryDto1.getName());

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getAllCategorys() {
        List<Category> entities = List.of(category1, category2);
        Page<Category> mockedPage = new PageImpl<>(entities);
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(mockedPage);

        Collection<CategoryDto> categoryDtoList = categoryService.getCategories(0, 10);

        assertEquals(categoryDtoList, List.of(categoryDto1, categoryDto2));

        verify(categoryRepository, times(1)).findAll(any(Pageable.class));
    }

}