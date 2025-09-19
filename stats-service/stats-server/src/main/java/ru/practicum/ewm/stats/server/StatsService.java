package ru.practicum.ewm.stats.server;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    HitDto saveHit(HitDto hit);

    @Transactional(readOnly = true)
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
