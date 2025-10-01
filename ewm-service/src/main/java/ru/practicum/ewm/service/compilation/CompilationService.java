package ru.practicum.ewm.service.compilation;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.service.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {


    CompilationDto createCompilation(NewCompilationDto request);

    void deleteCompilation(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, @PositiveOrZero Integer from, @Positive Integer size);

    CompilationDto getCompilationById(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request);
}
