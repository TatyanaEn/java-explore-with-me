package ru.practicum.ewm.service.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.categories.CategoryRepository;
import ru.practicum.ewm.service.categories.model.Category;
import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.QEvent;
import ru.practicum.ewm.service.exception.ConflictedDataException;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.service.user.UserMapper;
import ru.practicum.ewm.service.user.UserRepository;
import ru.practicum.ewm.service.user.UserService;
import ru.practicum.ewm.service.user.dto.UserDto;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.util.DateConstant;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;


    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateConstant.DATE_TIME_PATTERN)
            .withZone(ZoneOffset.UTC);

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                                         Boolean onlyAvailable, String sort, Integer from, Integer size){
        BooleanExpression bPredicate;
        BooleanExpression bText = QEvent.event.annotation.likeIgnoreCase(text).or(QEvent.event.description.likeIgnoreCase(text));
        bPredicate = bText;
        if (categories != null) {
            bPredicate.and(QEvent.event.category.id.in(categories));
        }
        if (paid != null) {
            bPredicate.and(QEvent.event.paid.eq(paid));
        }
        if (rangeStart.isBlank()) {
            rangeStart = dateTimeFormatter
                    .format(LocalDateTime.now());
        }
        bPredicate.and(QEvent.event.eventDate.after(LocalDateTime.parse(rangeStart, dateTimeFormatter)));

        if (!rangeEnd.isBlank()) {
            bPredicate.and(QEvent.event.eventDate.before(LocalDateTime.parse(rangeEnd, dateTimeFormatter)));
        }
        if (onlyAvailable != null) {
            // TODO onlyAvailable
        }
        Sort sortOrder = null;
        if (sort == "EVENT_DATE") {
            sortOrder = Sort.by("eventDate").ascending();
        }
        if (sort == "VIEWS") {
            //TODO SORT BY VIEWS
        }
        Pageable pageable = PageRequest.of(from / size, size, sortOrder);
        return eventRepository.findAll(bPredicate, pageable).stream().map(EventMapper::toEventShortDto).toList();

    }

    @Override
    public EventFullDto createEvent(NewEventDto newEvent, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. "
                        .formatted(userId), log));
        Category category = categoryRepository.findById(newEvent.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с ID '%d' не найдена. "
                        .formatted(newEvent.getCategory()), log));

        Event event = EventMapper.toEvent(newEvent);
        event.setInitiator(user);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEvent(UpdateEventAdminRequest request, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID '%d' не найдено. "
                        .formatted(eventId), log));
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictedDataException("Событие не в состоянии ожидания публикации.", log);
        }
        if (request.getStateAction().equals(EventStateAction.REJECT_EVENT)&&event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictedDataException("Событие нельзя отклонить, так как оно уже опубликовано.", log);
        }

    }




}
