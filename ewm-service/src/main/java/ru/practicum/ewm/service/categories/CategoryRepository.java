package ru.practicum.ewm.service.categories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.service.categories.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {


}
