package ru.practicum.ewm.stats.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.model.EndpointHit;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public HitDto saveHit(HitDto hit) {
        EndpointHit endpointHit = statsRepository.save(EndpointHitMapper.toEndpointHit(hit));
        return EndpointHitMapper.toEndpointHitDto(endpointHit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new DateTimeException("Не верно указан временной диапазон");
        }
        if (unique) {
            if (uris != null) {
                return statsRepository.findHitsWithUniqueIpWithUris(uris, start, end);
            }
            return statsRepository.findHitsWithUniqueIpWithoutUris(start, end);
        } else {
            if (uris != null) {
                return statsRepository.findAllHitsWithUris(uris, start, end);
            }
            return statsRepository.findAllHitsWithoutUris(start, end);
        }
    }
}