package ru.practicum.ewm.service.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserShortDto {
    Long id;
    @NotBlank
    @Size(max = 255)
    String name;

        public boolean hasName() {
        return !(name == null || name.isBlank());
    }
}
