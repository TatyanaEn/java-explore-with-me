package ru.practicum.ewm.service.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.service.categories.model.Category;

import java.time.LocalDateTime;

@Data
@Builder
public class EventFullDto {


    @NotBlank
    @NotNull
    @Size(max = 2000)
    private String annotation;

    @NotBlank
    @NotNull
    @Size(max = 2000)
    private String description;

    @NotBlank
    @NotNull
    private Category categoryId;

    private Long confirmedRequests;

    private String createdOn;

    @NotBlank
    @NotNull
    private String eventDate;

    private Long id;

    @NotBlank
    @NotNull
    private Long initiatorId;

    @NotBlank
    @NotNull
    private LocationDto location;

    @NotBlank
    @NotNull
    private Boolean paid;

    private Long participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration;
    private String state;
    @NotBlank
    @NotNull
    private String title;

    private Long views;

}
