package ru.practicum.ewm.service.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.service.categories.model.Category;
import ru.practicum.ewm.service.compilation.model.Compilation;
import ru.practicum.ewm.service.compilation.model.CompilationEvent;

import java.util.List;

public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {

    List<CompilationEvent> findByCompilationId(Long compilation_id);
}
