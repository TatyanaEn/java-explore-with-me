package ru.practicum.ewm.service.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.service.categories.model.Category;

@Data
@Builder
public class NewEventDto {


    @NotBlank
    @NotNull
    @Size(max = 2000)
    private String annotation;

    @NotBlank
    @NotNull
    private Category categoryId;

    private Long confirmedRequests;

    @NotBlank
    @NotNull
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
    private String title;

    private Long views;

}
