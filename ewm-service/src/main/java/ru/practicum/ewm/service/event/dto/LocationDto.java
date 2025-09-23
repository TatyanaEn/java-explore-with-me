package ru.practicum.ewm.service.event.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDto {

    private float lat;
    private float lon;

}
