package ru.practicum.ewm.service.event.dto.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.ewm.service.categories.CategoryMapper;
import ru.practicum.ewm.service.categories.CategoryRepository;
import ru.practicum.ewm.service.categories.dto.CategoryDto;
import ru.practicum.ewm.service.categories.model.Category;
import ru.practicum.ewm.service.event.EventService;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.LocationDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.event.mapper.EventMapper;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.EventState;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.user.UserMapper;
import ru.practicum.ewm.service.user.UserRepository;
import ru.practicum.ewm.service.user.dto.UserShortDto;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.util.DateConstant;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EventServiceImplTest {

    private final String stringTest = "Test";
    @Autowired
    private EventService eventService;
    @MockBean
    private EventRepository eventRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CategoryRepository categoryRepository;
    private Event event1;
    private User user1;
    private UserShortDto userDto;
    private NewEventDto eventDto1;
    private Category category1;
    private CategoryDto categoryDto;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);

        LocationDto loc = new LocationDto();
        loc.setLat(1L);
        loc.setLon(1L);

        category1 = Category.builder()
                .id(1L)
                .name("Фильмы")
                .build();

        categoryDto = CategoryMapper.toCategoryDto(category1);

        user1 = User.builder()
                .id(1L)
                .name("Tom")
                .email("cruise@test.com")
                .build();

        userDto = UserMapper.toUserShortDto(user1);


        EventFullDto eventDto = EventFullDto.builder()
                .id(1L)
                .annotation(stringTest)
                .description(stringTest)
                .category(categoryDto)
                .confirmedRequests(10L)
                .createdOn(DateConstant.dateTimeFormatter
                        .format(LocalDateTime.now()))
                .eventDate(DateConstant.dateTimeFormatter
                        .format(LocalDateTime.now().plusDays(30)))
                .initiator(userDto)
                .paid(true)
                .participantLimit(10L)
                .publishedOn(DateConstant.dateTimeFormatter
                        .format(LocalDateTime.now()))
                .requestModeration(true)
                .state("PENDING")
                .title(stringTest)
                .views(10L)
                .build();


        eventDto1 = NewEventDto.builder()
                .id(1L)
                .annotation(stringTest)
                .description(stringTest)
                .category(categoryDto.getId())
                .location(loc)
                .eventDate(DateConstant.dateTimeFormatter
                        .format(LocalDateTime.now().plusDays(30)))
                .initiatorId(userDto.getId())
                .paid(true)
                .participantLimit(10L)
                .requestModeration(true)
                .title(stringTest)
                .build();

        event1 = EventMapper.toEvent(eventDto1);
        event1.setCreatedOn(LocalDateTime.now());
        event1.setInitiator(user1);
        event1.setCategory(CategoryMapper.toCategory(categoryDto));
        event1.setState(EventState.PENDING);


        MockitoAnnotations.openMocks(this);

    }

    @Test
    void addEvent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category1));
        when(eventRepository.save(any(Event.class))).thenReturn(event1);


        EventFullDto eventDtoTest = eventService.createEvent(eventDto1, 1L);

        assertEquals(eventDtoTest.getId(), eventDto1.getId());
        assertEquals(eventDtoTest.getAnnotation(), eventDto1.getAnnotation());
        assertEquals(eventDtoTest.getDescription(), eventDto1.getDescription());
        assertEquals(eventDtoTest.getCategory().getId(), eventDto1.getCategory());
        assertEquals(eventDtoTest.getLocation().getLon(), eventDto1.getLocation().getLon());
        assertEquals(eventDtoTest.getLocation().getLat(), eventDto1.getLocation().getLat());
        assertEquals(eventDtoTest.getEventDate(), eventDto1.getEventDate());
        assertEquals(eventDtoTest.getInitiator().getId(), eventDto1.getInitiatorId());
        assertEquals(eventDtoTest.getPaid(), eventDto1.getPaid());
        assertEquals(eventDtoTest.getTitle(), eventDto1.getTitle());
        assertEquals(eventDtoTest.getParticipantLimit(), eventDto1.getParticipantLimit());
        assertEquals(eventDtoTest.getRequestModeration(), eventDto1.getRequestModeration());
        verify(eventRepository, times(1)).save(any(Event.class));
    }
}
