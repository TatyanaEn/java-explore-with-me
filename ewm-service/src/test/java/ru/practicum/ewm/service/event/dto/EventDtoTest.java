package ru.practicum.ewm.service.event.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewm.service.categories.dto.CategoryDto;
import ru.practicum.ewm.service.user.dto.UserShortDto;
import ru.practicum.ewm.service.util.DateConstant;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class EventDtoTest {
    @Autowired
    private JacksonTester<EventFullDto> json;


    @Test
    void testEventFullDto() throws Exception {

        CategoryDto categoryDto = CategoryDto.builder()
                .id(1L)
                .name("Test")
                .build();
        UserShortDto userDto = UserShortDto.builder()
                .id(1L)
                .name("Test")
                .build();
        EventFullDto eventDto = EventFullDto.builder()
                .id(1L)
                .annotation("Test")
                .description("Test")
                .category(categoryDto)
                .confirmedRequests(10L)
                .createdOn(DateConstant.dateTimeFormatter
                        .format(LocalDateTime.now()))
                .eventDate(DateConstant.dateTimeFormatter
                        .format(LocalDateTime.now()))
                .initiator(userDto)
                .paid(true)
                .participantLimit(10L)
                .publishedOn(DateConstant.dateTimeFormatter
                        .format(LocalDateTime.now()))
                .requestModeration(true)
                .state("PENDING")
                .title("Test")
                .views(10L)
                .build();


        JsonContent<EventFullDto> result = json.write(eventDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.annotation").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.category.name").isEqualTo(categoryDto.getName());
        assertThat(result).extractingJsonPathNumberValue("$.confirmedRequests").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.createdOn").isEqualTo(DateConstant.dateTimeFormatter
                .format(LocalDateTime.now()));
        assertThat(result).extractingJsonPathStringValue("$.eventDate").isEqualTo(DateConstant.dateTimeFormatter
                .format(LocalDateTime.now()));
        assertThat(result).extractingJsonPathStringValue("$.initiator.name").isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathBooleanValue("$.paid").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.participantLimit").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.publishedOn").isEqualTo(DateConstant.dateTimeFormatter
                .format(LocalDateTime.now()));
        assertThat(result).extractingJsonPathBooleanValue("$.requestModeration").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.state").isEqualTo("PENDING");
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo("Test");
        assertThat(result).extractingJsonPathNumberValue("$.views").isEqualTo(10);


    }
}
