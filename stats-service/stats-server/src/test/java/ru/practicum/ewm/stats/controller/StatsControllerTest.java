package ru.practicum.ewm.stats.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.StatsController;
import ru.practicum.ewm.stats.server.StatsServer;
import ru.practicum.ewm.stats.server.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ContextConfiguration(classes = {StatsServer.class})
class StatsControllerTest {
    final LocalDateTime testDate = LocalDateTime.of(2020, 2, 2, 2, 2, 2);
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    StatsService statsService;

    @SneakyThrows
    @Test
    void create_whenInvoked_thenReturnResponseStatusCreated() {
        mvc.perform(MockMvcRequestBuilders.post("/hit")
                        .content(mapper.writeValueAsString(new HitDto("testApp", "testUri", "testIp",
                                testDate)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(statsService).saveHit(any());
    }

    @SneakyThrows
    @Test
    void get_whenInvoked_thenReturnResponseStatusOkWithCollectionViewStats() {
        when(statsService.getStats(any(), any(), any(), any())).thenReturn(List.of(new ViewStatsDto("testApp",
                "testUri", 5L)));

        mvc.perform(MockMvcRequestBuilders.get("/stats")
                        .param("start", "2020-02-02 02:02:02")
                        .param("end", "2020-02-02 02:02:02")
                        .param("uris", List.of().toString())
                        .param("unique", "FALSE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].app").value("testApp"))
                .andExpect(jsonPath("$.[0].uri").value("testUri"))
                .andExpect(jsonPath("$.[0].hits").value(5L));
    }
}