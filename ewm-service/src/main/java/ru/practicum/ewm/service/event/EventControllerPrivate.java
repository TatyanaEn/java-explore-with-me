package ru.practicum.ewm.service.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;

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


    /*@DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable("catId") Long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateEvent(@PathVariable("catId") long catId, @RequestBody EventDto request) {
        request.setId(catId);
        return eventService.updateEvent(request);
    }*/
}
