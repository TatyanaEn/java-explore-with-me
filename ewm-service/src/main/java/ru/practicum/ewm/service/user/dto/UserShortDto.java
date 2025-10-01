package ru.practicum.ewm.service.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {
    Long id;
    @NotBlank
    @Size(max = 255)
    String name;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }
}
