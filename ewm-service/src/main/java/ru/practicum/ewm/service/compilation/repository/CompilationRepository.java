package ru.practicum.ewm.service.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.service.categories.model.Category;
import ru.practicum.ewm.service.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation , Long> , QuerydslPredicateExecutor<Compilation> {

    List<Compilation> findByTitle(String title);

}
