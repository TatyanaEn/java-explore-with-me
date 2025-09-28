package ru.practicum.ewm.service.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.service.event.EventServiceImpl;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.service.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.event.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.event.dto.UpdateEventUserRequest;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class EventControllerPrivate {

    private final EventServiceImpl eventService;


    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") Long userId,
                                    @Valid @RequestBody NewEventDto eventRequest) {
        return eventService.createEvent(eventRequest, userId);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsByUser(@PathVariable("userId") Long userId,
                                               @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByUser(@PathVariable("userId") Long userId,
                                       @PathVariable("eventId") Long eventId) {
        return eventService.getEventByUser(userId, eventId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable("userId") Long userId,
                                                 @RequestParam(value = "eventId") Long eventId) {
        return eventService.createRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsByUser(@PathVariable("userId") Long userId) {
        return eventService.getRequestsByUser(userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByEvent(@PathVariable("userId") Long userId,
                                                            @PathVariable("eventId") Long eventId) {
        return eventService.getRequestsByEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByUser(@PathVariable("userId") long userId,
                                          @PathVariable("eventId") long eventId,
                                          @Valid @RequestBody UpdateEventUserRequest request) {
        return eventService.updateEventByUser(userId, eventId, request);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestByUser(@PathVariable("userId") long userId,
                                                              @PathVariable("eventId") long eventId,
                                                              @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        return eventService.updateRequestByUser(userId, eventId, request);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequestByUser(@PathVariable("userId") long userId,
                                                       @PathVariable("requestId") long requestId) {
        return eventService.cancelRequestByUser(userId, requestId);
    }


}
