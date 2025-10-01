package ru.practicum.ewm.service.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    private List<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50)
    private String title;

    public boolean hasTitle() {
        return !(title == null || title.isBlank());
    }

    public boolean hasPinned() {
        return !(pinned == null);
    }
}
