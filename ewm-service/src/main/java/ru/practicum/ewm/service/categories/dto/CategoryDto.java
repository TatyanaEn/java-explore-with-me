package ru.practicum.ewm.service.categories.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    @Id
    private Long id;
    @NotBlank
    @Size(min = 1,  max = 50)
    String name;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }
}
