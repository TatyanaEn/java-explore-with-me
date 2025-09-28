package ru.practicum.ewm.service.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.service.categories.CategoryMapper;
import ru.practicum.ewm.service.categories.model.Category;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.LocationDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.service.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.EventState;
import ru.practicum.ewm.service.event.model.EventStateAction;
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
                .paid(eventDto.getPaid() != null && eventDto.getPaid())
                .title(eventDto.getTitle())
                .participantLimit(eventDto.getParticipantLimit() == null ? 0 : eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration() == null || eventDto.getRequestModeration())
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
                //.confirmedRequests(confirmedRequestsCount)
                .createdOn(dateTimeFormatter
                        .format(event.getCreatedOn()))
                .eventDate(dateTimeFormatter
                        .format(event.getEventDate()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationDto.builder().lat(event.getLat()).lon(event.getLon()).build())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn() == null ? null : dateTimeFormatter.format(event.getPublishedOn()))
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .state(event.getState().toString())
                //.views(viewsCount)
                .build();
    }


    public static Event updateAdminEventFields(Event event, UpdateEventAdminRequest request, Category category) {
        if (request.hasAnnotation())
            event.setAnnotation(request.getAnnotation());
        if (request.hasDescription())
            event.setDescription(request.getDescription());
        if (request.hasCategory())
            event.setCategory(category);
        if (request.hasLocation()) {
            event.setLat(request.getLocation().getLat());
            event.setLon(request.getLocation().getLon());
        }
        if (request.hasPaid())
            event.setPaid(request.getPaid());
        if (request.hasParticipantLimit()) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.hasRequestModeration()) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.hasTitle()) {
            event.setTitle(request.getTitle());
        }
        if (request.hasStateAction()) {
            if (request.getStateAction().equals((EventStateAction.PUBLISH_EVENT))) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (request.getStateAction().equals((EventStateAction.REJECT_EVENT))) {

                event.setState(EventState.CANCELED);
            }
        }

        return event;
    }

    public static Event updateUserEventFields(Event event, UpdateEventUserRequest request, Category category) {
        if (request.hasAnnotation())
            event.setAnnotation(request.getAnnotation());
        if (request.hasDescription())
            event.setDescription(request.getDescription());
        if (request.hasCategory())
            event.setCategory(category);
        if (request.hasLocation()) {
            event.setLat(request.getLocation().getLat());
            event.setLon(request.getLocation().getLon());
        }
        if (request.hasPaid())
            event.setPaid(request.getPaid());
        if (request.hasParticipantLimit()) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.hasRequestModeration()) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.hasTitle()) {
            event.setTitle(request.getTitle());
        }
        if (request.hasStateAction()) {
            if (request.getStateAction().equals((EventStateAction.SEND_TO_REVIEW.toString()))) {
                event.setState(EventState.PENDING);
            }
            if (request.getStateAction().equals((EventStateAction.CANCEL_REVIEW.toString()))) {
                event.setState(EventState.CANCELED);
            }
        }

        return event;
    }


}