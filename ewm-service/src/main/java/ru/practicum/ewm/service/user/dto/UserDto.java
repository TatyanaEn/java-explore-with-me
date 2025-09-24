package ru.practicum.ewm.service.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    Long id;
    @NotBlank
    @Size(max = 255)
    String name;
    @Email
    @NotBlank
    @NotNull
    @Size(max = 512)
    String email;

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }
}
