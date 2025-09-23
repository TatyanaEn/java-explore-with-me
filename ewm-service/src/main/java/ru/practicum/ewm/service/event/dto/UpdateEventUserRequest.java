package ru.practicum.ewm.service.event.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.service.categories.model.Category;


@Data
@Builder
public class UpdateEventUserRequest {
    @Size(max = 2000)
    private String annotation;

    @Size(max = 2000)
    private String description;

    private Category categoryId;

    private String eventDate;

    private LocationDto location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    private String title;
}
