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
import ru.practicum.ewm.service.categories.dto.CategoryDto;
import ru.practicum.ewm.service.categories.model.Category;
import ru.practicum.ewm.service.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.ewm.service.util.DateConstant.DATE_TIME_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {


    @NotBlank
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private CategoryDto category;

    private Long confirmedRequests;

    @NotBlank
    @NotNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private String eventDate;

    private Long id;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Boolean paid;

    @NotBlank
    @NotNull
    @Size(min = 3, max = 120)
    private String title;

    private Long views;

}
