package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.service.categories.CategoryMapper;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.LocationDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.user.UserMapper;
import ru.practicum.ewm.service.util.DateConstant;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public final class EventMapper {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateConstant.DATE_TIME_PATTERN)
            .withZone(ZoneOffset.UTC);


    public static Event toEvent(NewEventDto eventDto) {
        return Event.builder()
                .id(eventDto.getId())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .lat(eventDto.getLocation().getLat())
                .lon(eventDto.getLocation().getLon())
                .eventDate(LocalDateTime.parse(eventDto.getEventDate(), dateTimeFormatter))
                .paid(eventDto.getPaid())
                .title(eventDto.getTitle())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                //.confirmedRequests()
                .eventDate(dateTimeFormatter
                        .format(event.getEventDate()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                //.views()
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                //.confirmedRequests()
                .createdOn(dateTimeFormatter
                        .format(event.getCreatedOn()))
                .eventDate(dateTimeFormatter
                        .format(event.getEventDate()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationDto.builder().lat(event.getLat()).lon(event.getLon()).build())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(dateTimeFormatter
                        .format(event.getPublishedOn()))
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                //.views()
                .build();
    }


    /*public static Event updateEventFields(Event event, EventDto eventDto) {
        if (eventDto.hasEmail())
            event.setEmail(eventDto.getEmail());
        if (eventDto.hasName())
            event.setName(eventDto.getName());
        return event;
    }*/

}