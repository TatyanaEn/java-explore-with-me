package ru.practicum.ewm.stats.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.ewm.stats.server.StatsServer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@ContextConfiguration(classes = {StatsServer.class})
public class ViewStatsDtoTest {
    @Autowired
    private JacksonTester<ViewStatsDto> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialize() {
        ViewStatsDto viewStats = new ViewStatsDto("testApp", "testUri", 5L);

        JsonContent<ViewStatsDto> result = jacksonTester.write(viewStats);

        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo("testApp");
        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo("testUri");
        assertThat(result).extractingJsonPathNumberValue("$.hits").isEqualTo(5);
    }
}
