package ru.practicum.ewm.service.compilation.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    @Id
    private Long id;

    private List<EventShortDto> events;

    @NotNull
    private Boolean pinned;

    @NotBlank
    @NotNull
    @Size(min = 1, max = 50)
    private String title;

    public boolean hasTitle() {
        return !(title == null || title.isBlank());
    }

    public boolean hasPinned() {
        return !(pinned == null);
    }

}
