package ru.practicum.ewm.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.categories.dto.CategoryDto;
import ru.practicum.ewm.service.categories.model.Category;
import ru.practicum.ewm.service.user.dto.UserShortDto;

import static ru.practicum.ewm.service.util.DateConstant.DATE_TIME_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {


    @NotBlank
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotBlank
    @NotNull
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    private CategoryDto category;

    private Long confirmedRequests;

    private String createdOn;

    @NotBlank
    @NotNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private String eventDate;

    private Long id;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private LocationDto location;

    @NotNull
    private Boolean paid;

    @Min(value = 0, message = "Значение не может быть отрицательным")
    private Long participantLimit;

    private String publishedOn;

    private Boolean requestModeration;
    private String state;
    @NotBlank
    @NotNull
    @Size(min = 3, max = 120)
    private String title;

    private Long views;

}
