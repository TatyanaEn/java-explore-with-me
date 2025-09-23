package ru.practicum.ewm.service.event;

import ru.practicum.ewm.service.categories.dto.CategoryDto;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.event.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventService {

    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                                  Boolean onlyAvailable, String sort, Integer from, Integer size);

    List<EventShortDto> getEventsByUserId(Integer from, Integer size, Long userId);

    EventFullDto getEventsById(Long eventId);

    EventFullDto createEvent(NewEventDto newEvent, Long userId);

    EventFullDto updateEvent(UpdateEventUserRequest request, Long userId, Long eventId);

    void deleteCategory(Long catId);
}
