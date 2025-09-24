package ru.practicum.ewm.service.event;

import ru.practicum.ewm.service.categories.dto.CategoryDto;
import ru.practicum.ewm.service.event.dto.*;

import java.util.List;

public interface EventService {

    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                                  Boolean onlyAvailable, String sort, Integer from, Integer size);

    EventFullDto createEvent(NewEventDto newEvent, Long userId);

    EventFullDto updateEvent(UpdateEventAdminRequest request,  Long eventId);

    /*List<EventShortDto> getEventsByUserId(Integer from, Integer size, Long userId);

    EventFullDto getEventsById(Long eventId);



    EventFullDto updateEvent(UpdateEventUserRequest request, Long userId, Long eventId);

    void deleteCategory(Long catId);*/
}
