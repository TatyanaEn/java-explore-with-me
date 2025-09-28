package ru.practicum.ewm.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static ru.practicum.ewm.service.util.DateConstant.DATE_TIME_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {


    @NotBlank
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotBlank
    @NotNull
    @Size(min = 20, max = 7000)
    private String description;

    private Long category;

    private LocationDto location;

    @NotBlank
    @NotNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private String eventDate;

    private Long id;

    private Long initiatorId;

    private Boolean paid;

    @NotBlank
    @NotNull
    @Size(min = 3, max = 120)
    private String title;

    @Min(value = 0, message = "Значение не может быть отрицательным")
    private Long participantLimit;

    private Boolean requestModeration;
}
