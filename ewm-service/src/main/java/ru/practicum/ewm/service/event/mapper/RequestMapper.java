package ru.practicum.ewm.service.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.service.event.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.event.model.Request;
import ru.practicum.ewm.service.util.DateConstant;

@Component
@RequiredArgsConstructor
public final class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(DateConstant.dateTimeFormatter
                        .format(request.getCreated()))
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .status(request.getStatus().toString())
                .build();
    }


}