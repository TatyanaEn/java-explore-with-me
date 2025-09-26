package ru.practicum.ewm.service.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.service.event.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.event.model.Request;
import ru.practicum.ewm.service.util.DateConstant;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public final class RequestMapper {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateConstant.DATE_TIME_PATTERN)
            .withZone(ZoneOffset.UTC);



    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(dateTimeFormatter
                        .format(request.getCreated()))
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .status(request.getStatus().toString())
                .build();
    }


}