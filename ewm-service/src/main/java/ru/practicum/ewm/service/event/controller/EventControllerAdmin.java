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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.service.event.EventServiceImpl;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.UpdateEventAdminRequest;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
@Validated
public class EventControllerAdmin {

    private final EventServiceImpl eventService;


    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable("eventId") long eventId, @Valid @RequestBody UpdateEventAdminRequest request) {
        return eventService.updateEventByAdmin(request, eventId);
    }

    @GetMapping
    public List<EventFullDto> getEventsByAdmin(@RequestParam(value = "users", required = false) List<Long> users,
                                               @RequestParam(value = "states", required = false) List<String> states,
                                               @RequestParam(value = "categories", required = false) List<Long> categories,
                                               @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                               @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                               @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(value = "size", required = false, defaultValue = "10") @Positive Integer size) {
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }


}
