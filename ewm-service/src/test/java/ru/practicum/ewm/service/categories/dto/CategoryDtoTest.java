package ru.practicum.ewm.service.categories.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CategoryDtoTest {

    @Autowired
    private JacksonTester<CategoryDto> json;

    @Test
    void testCategoryDto() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1L)
                .name("Test")
                .build();

        JsonContent<CategoryDto> result = json.write(categoryDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test");
    }
}
