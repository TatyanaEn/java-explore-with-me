package ru.practicum.ewm.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.service.compilation.model.Compilation;

@Component
@RequiredArgsConstructor
public final class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto compilationDto) {
        return Compilation.builder()
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .build();
    }

    public static Compilation updateCompilationFields(Compilation compilation, UpdateCompilationRequest compilationDto) {
        if (compilationDto.hasTitle())
            compilation.setTitle(compilationDto.getTitle());
        if (compilationDto.hasPinned())
            compilation.setPinned(compilationDto.getPinned());
        return compilation;
    }

}