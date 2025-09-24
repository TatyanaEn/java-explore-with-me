package ru.practicum.ewm.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.categories.model.Category;

import static ru.practicum.ewm.service.util.DateConstant.DATE_TIME_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {


    @NotBlank
    @NotNull
    @Size(max = 2000)
    private String annotation;

    @NotBlank
    @NotNull
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    private Long category;

    @NotNull
    private LocationDto location;

    @NotBlank
    @NotNull
    @Future
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private String eventDate;

    private Long id;

    @NotBlank
    @NotNull
    private Long initiatorId;

    @NotBlank
    @NotNull
    private Boolean paid;

    @NotBlank
    @NotNull
    @Size(min = 3, max = 120)
    private String title;


}
