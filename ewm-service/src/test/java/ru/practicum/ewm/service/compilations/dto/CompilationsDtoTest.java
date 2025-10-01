package ru.practicum.ewm.service.compilations.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewm.service.categories.dto.CategoryDto;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.user.dto.UserShortDto;
import ru.practicum.ewm.service.util.DateConstant;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CompilationsDtoTest {

    @Autowired
    private JacksonTester<CompilationDto> json;

    @Test
    void testCompilationDto() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1L)
                .name("Test")
                .build();
        UserShortDto userDto = UserShortDto.builder()
                .id(1L)
                .name("Test")
                .build();
        EventShortDto eventDto = EventShortDto.builder()
                .id(1L)
                .annotation("Test")
                .category(categoryDto)
                .confirmedRequests(10L)
                .eventDate(DateConstant.dateTimeFormatter
                        .format(LocalDateTime.now()))
                .initiator(userDto)
                .paid(true)
                .title("Test")
                .views(10L)
                .build();
        CompilationDto compilationDto = CompilationDto.builder()
                .id(1L)
                .pinned(true)
                .title("Test")
                .events(List.of(eventDto))
                .build();

        JsonContent<CompilationDto> result = json.write(compilationDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathBooleanValue("$.pinned").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo("Test");
    }
}
