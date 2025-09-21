package ru.practicum.ewm.stats.server;


import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.server.model.EndpointHit;

@UtilityClass
public class EndpointHitMapper {
    public EndpointHit toEndpointHit(HitDto hit) {
        return new EndpointHit(
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp()
        );
    }

    public HitDto toEndpointHitDto(EndpointHit hit) {
        return new HitDto(
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp()
        );
    }
}