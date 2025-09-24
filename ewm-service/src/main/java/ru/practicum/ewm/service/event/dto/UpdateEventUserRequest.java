package ru.practicum.ewm.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
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
public class UpdateEventUserRequest {
    @Size(max = 2000)
    private String annotation;

    @Size(min = 20, max = 7000)
    private String description;

    private Category categoryId;

    @Future
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private String eventDate;

    private LocationDto location;

    private Boolean paid;

    @Positive
    private Long participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
