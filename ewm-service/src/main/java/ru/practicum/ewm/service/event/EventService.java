package ru.practicum.ewm.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.service.event.dto.*;

import java.util.List;

public interface EventService {

    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                                  Boolean onlyAvailable, String sort, Integer from, Integer size,
                                  HttpServletRequest request);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                        String rangeStart, String rangeEnd,
                                        Integer from, Integer size);

    EventFullDto  getEvent(Long eventId, HttpServletRequest request);

    EventFullDto getEventByUser(Long userId, Long eventId);

    EventFullDto createEvent(NewEventDto newEvent, Long userId);

    EventFullDto updateEventByAdmin(UpdateEventAdminRequest request,  Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest request);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByUser(Long userId);

    List<ParticipationRequestDto> getRequestsByEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestByUser(long userId, long eventId, EventRequestStatusUpdateRequest request);

    ParticipationRequestDto cancelRequestByUser(long userId, long requestId);
}
