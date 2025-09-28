package ru.practicum.ewm.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
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
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    @Size(min = 20, max = 7000)
    private String description;

    private Long category;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private String eventDate;

    private LocationDto location;

    private Boolean paid;

    @Min(value = 0, message = "Значение не может быть отрицательным")
    private Long participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(min = 3, max = 120)
    private String title;

    public boolean hasAnnotation() {
        return !(annotation == null || annotation.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasCategory() {
        return !(category == null);
    }

    public boolean hasLocation() {
        return !(location == null);
    }

    public boolean hasPaid() {
        return !(paid == null);
    }

    public boolean hasParticipantLimit() {
        return !(participantLimit == null);
    }

    public boolean hasRequestModeration() {
        return !(requestModeration == null);
    }

    public boolean hasStateAction() {
        return !(stateAction == null);
    }

    public boolean hasTitle() {
        return !(title == null || title.isBlank());
    }

}
