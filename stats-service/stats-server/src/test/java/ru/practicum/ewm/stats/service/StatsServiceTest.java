package ru.practicum.ewm.stats.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.StatsRepository;
import ru.practicum.ewm.stats.server.StatsServer;
import ru.practicum.ewm.stats.server.StatsService;
import ru.practicum.ewm.stats.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = {StatsServer.class})
public class StatsServiceTest {

    final LocalDateTime testDate = LocalDateTime.of(2020, 2, 2, 2, 2, 2);
    @Autowired
    private StatsService statsService;
    @MockBean
    private StatsRepository statsRepository;
    private HitDto hitDto;
    private EndpointHit hit;
    private ViewStatsDto viewStats;

    @BeforeEach
    void beforeEach() {

        LocalDateTime date = LocalDateTime.of(2023, 12, 12, 9, 0);

        hit = EndpointHit.builder()
                .id(1L)
                .app("ewm-main-service")
                .ip("192.163.0.1")
                .uri("/events/1")
                .timestamp(date)
                .build();

        hitDto = HitDto.builder()
                .app(hit.getApp())
                .ip(hit.getIp())
                .uri(hit.getUri())
                .timestamp(hit.getTimestamp())
                .build();

        viewStats = new ViewStatsDto("testApp", "testUri", 1L);
    }

    @Test
    public void saveHitTest() {

        when(statsRepository.save(any(EndpointHit.class))).thenReturn(hit);

        HitDto hitDtoTest = statsService.saveHit(hitDto);

        assertEquals(hitDtoTest.getApp(), hit.getApp());
        assertEquals(hitDtoTest.getIp(), hit.getIp());
        assertEquals(hitDtoTest.getUri(), hit.getUri());
        assertEquals(hitDtoTest.getTimestamp(), hit.getTimestamp());

        verify(statsRepository, times(1)).save(any(EndpointHit.class));

    }

    @Test
    public void getStatsWhenUniqueFalseWithUris() {
        when(statsRepository.findAllHitsWithUris(any(), any(), any())).thenReturn(List.of(viewStats));

        List<ViewStatsDto> actualViewStats = statsService.getStats(testDate, testDate, List.of("testUris"),
                Boolean.FALSE);

        assertFalse(actualViewStats.isEmpty());
        assertEquals(1, actualViewStats.size());
        assertEquals(List.of(viewStats), actualViewStats);

        verify(statsRepository, times(1)).findAllHitsWithUris(any(), any(), any());

    }

    @Test
    public void getStatsWhenUniqueFalseWithOutUris() {
        when(statsRepository.findAllHitsWithoutUris(any(), any())).thenReturn(List.of(viewStats));

        List<ViewStatsDto> actualViewStats = statsService.getStats(testDate, testDate, null,
                Boolean.FALSE);

        assertFalse(actualViewStats.isEmpty());
        assertEquals(1, actualViewStats.size());
        assertEquals(List.of(viewStats), actualViewStats);

        verify(statsRepository, times(1)).findAllHitsWithoutUris(any(), any());

    }

    @Test
    public void getStatsWhenUniqueTrueWithUris() {
        when(statsRepository.findHitsWithUniqueIpWithUris(any(), any(), any())).thenReturn(List.of(viewStats));

        List<ViewStatsDto> actualViewStats = statsService.getStats(testDate, testDate, List.of("testUris"),
                Boolean.TRUE);

        assertFalse(actualViewStats.isEmpty());
        assertEquals(1, actualViewStats.size());
        assertEquals(List.of(viewStats), actualViewStats);

        verify(statsRepository, times(1)).findHitsWithUniqueIpWithUris(any(), any(), any());

    }

    @Test
    public void getStatsWhenUniqueTrueWithOutUris() {
        when(statsRepository.findHitsWithUniqueIpWithoutUris(any(), any())).thenReturn(List.of(viewStats));

        List<ViewStatsDto> actualViewStats = statsService.getStats(testDate, testDate, null,
                Boolean.TRUE);

        assertFalse(actualViewStats.isEmpty());
        assertEquals(1, actualViewStats.size());
        assertEquals(List.of(viewStats), actualViewStats);

        verify(statsRepository, times(1)).findHitsWithUniqueIpWithoutUris(any(), any());

    }


}
