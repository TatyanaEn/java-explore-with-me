package ru.practicum.ewm.service.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.service.compilation.model.CompilationEvent;

import java.util.List;

public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {

    List<CompilationEvent> findByCompilationId(Long compilationId);
}
