package ru.practicum.ewm.service.categories;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.categories.dto.CategoryDto;
import ru.practicum.ewm.service.categories.model.Category;
import ru.practicum.ewm.service.exception.NotFoundException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
@Validated
public class CategoryControllerAdmin {

    private final CategoryServiceImpl categoryService;
    

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto categoryRequest) {
        return categoryService.createCategory(categoryRequest);
    }


    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable("catId") long catId, @RequestBody CategoryDto request) {
        request.setId(catId);
        return categoryService.updateCategory(request);
    }
}
