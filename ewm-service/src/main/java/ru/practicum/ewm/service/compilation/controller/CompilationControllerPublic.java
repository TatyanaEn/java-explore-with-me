package ru.practicum.ewm.service.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.service.compilation.CompilationServiceImpl;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
@Validated
public class CompilationControllerPublic {

    private final CompilationServiceImpl compilationService;


    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(value = "pinned", required = false)  Boolean pinned,
                                                @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable("compId") Long compId) {
        return compilationService.getCompilationById(compId);
    }


}
