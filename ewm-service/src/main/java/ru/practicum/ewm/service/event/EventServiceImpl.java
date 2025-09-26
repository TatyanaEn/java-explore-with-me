package ru.practicum.ewm.service.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.ewm.service.categories.CategoryRepository;
import ru.practicum.ewm.service.categories.model.Category;
import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.event.mapper.EventMapper;
import ru.practicum.ewm.service.event.mapper.RequestMapper;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.EventState;
import ru.practicum.ewm.service.event.model.EventStateAction;
import ru.practicum.ewm.service.event.model.QEvent;
import ru.practicum.ewm.service.event.model.Request;
import ru.practicum.ewm.service.event.model.RequestStatus;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.repository.RequestRepository;
import ru.practicum.ewm.service.exception.ConflictedDataException;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.service.exception.ValidationException;
import ru.practicum.ewm.service.user.UserRepository;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.util.DateConstant;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;


    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateConstant.DATE_TIME_PATTERN)
            .withZone(ZoneOffset.UTC);

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,String rangeStart, String rangeEnd,
                                                Integer from, Integer size){
        BooleanExpression bPredicate;
        bPredicate = Expressions.asBoolean(true).isTrue();
        if (users != null) {
            bPredicate.and(QEvent.event.initiator.id.in(users));
        }
        List<EventState> enumList = new ArrayList<>();
        if (states != null) {
            for (String str : states) {
                EventState myEnum = EventState.valueOf(str);
                enumList.add(myEnum);
            }
        }
        bPredicate.and(QEvent.event.state.in(enumList));
        if (categories != null) {
            bPredicate.and(QEvent.event.category.id.in(categories));
        }
        if (rangeStart == null || rangeStart.isBlank()) {
            rangeStart = dateTimeFormatter
                    .format(LocalDateTime.now());
        }
        if (!(rangeStart == null || rangeStart.isBlank())) {
            bPredicate.and(QEvent.event.eventDate.after(LocalDateTime.parse(rangeStart, dateTimeFormatter)));
        }

        if (!(rangeEnd == null || rangeEnd.isBlank())) {
            bPredicate.and(QEvent.event.eventDate.before(LocalDateTime.parse(rangeEnd, dateTimeFormatter)));
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("eventDate").descending());

        return eventRepository.findAll(bPredicate, pageable).stream().map(EventMapper::toEventFullDto).toList();

    }

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        BooleanExpression bPredicate;
        bPredicate = QEvent.event.initiator.id.eq(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAll(bPredicate, pageable).stream().map(EventMapper::toEventShortDto).toList();

    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,String rangeStart, String rangeEnd,
                                         Boolean onlyAvailable, String sort, Integer from, Integer size){
        BooleanExpression bPredicate = QEvent.event.state.eq(EventState.PUBLISHED);
        if (!(text == null || text.isBlank())) {
            BooleanExpression bText = QEvent.event.annotation.likeIgnoreCase(text).or(QEvent.event.description.likeIgnoreCase(text));
            bPredicate = bPredicate.and(bText);
        }
        if (categories != null) {
            bPredicate.and(QEvent.event.category.id.in(categories));
        }
        if (paid != null) {
            bPredicate.and(QEvent.event.paid.eq(paid));
        }
        if (rangeStart == null || rangeStart.isBlank()) {
            rangeStart = dateTimeFormatter
                    .format(LocalDateTime.now());
        }
        if (!(rangeStart == null || rangeStart.isBlank())) {
            bPredicate.and(QEvent.event.eventDate.after(LocalDateTime.parse(rangeStart, dateTimeFormatter)));
        }

        if (!(rangeEnd == null || rangeEnd.isBlank())) {
            bPredicate.and(QEvent.event.eventDate.before(LocalDateTime.parse(rangeEnd, dateTimeFormatter)));
        }
        if (onlyAvailable != null) {
            // TODO onlyAvailable
        }
        Pageable pageable = null;
        if (sort != null) {
            Sort sortOrder = null;
            if (Objects.equals(sort, "EVENT_DATE")) {
                sortOrder = Sort.by("eventDate").ascending();
            }
            if (Objects.equals(sort, "VIEWS")) {
                //TODO SORT BY VIEWS
            }
            pageable = PageRequest.of(from / size, size, sortOrder);
        } else {
            pageable = PageRequest.of(from / size, size);
        }
        return eventRepository.findAll(bPredicate, pageable).stream().map(EventMapper::toEventShortDto).toList();

    }

    @Override
    public EventFullDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID '%d' не найдено. ".formatted(eventId), log));
        if (event.getState().equals(EventState.PUBLISHED))
            return EventMapper.toEventFullDto(event);
        else
            throw new NotFoundException("Событие не опубликовано", log);
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID '%d' не найдено. ".formatted(eventId), log));
        if (!Objects.equals(event.getInitiator().getId(), userId))
            throw new NotFoundException("Пользователь не является инициатором события с ID '%d'.".formatted(eventId), log);
        return EventMapper.toEventFullDto(event);
    }


    @Override
    public EventFullDto createEvent(NewEventDto newEvent, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. "
                        .formatted(userId), log));
        Category category = categoryRepository.findById(newEvent.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с ID '%d' не найдена. "
                        .formatted(newEvent.getCategory()), log));
        if (newEvent.getEventDate() != null)
            if (LocalDateTime.parse(newEvent.getEventDate(), dateTimeFormatter).isBefore(LocalDateTime.now())) {
                throw new ValidationException("Дата события не может быть меньше текущей даты", log);
            }
        Event event = EventMapper.toEvent(newEvent);
        event.setInitiator(user);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEventByAdmin(UpdateEventAdminRequest request, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID '%d' не найдено. "
                        .formatted(eventId), log));
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictedDataException("Событие не в состоянии ожидания публикации.", log);
        }
        if (request.getStateAction() != null)
            if ((request.getStateAction().equals(EventStateAction.REJECT_EVENT)) && event.getState().equals(EventState.PUBLISHED)) {
                throw new ConflictedDataException("Событие нельзя отклонить, так как оно уже опубликовано.", log);
            }
        if (request.getEventDate() != null)
            if (LocalDateTime.parse(request.getEventDate(), dateTimeFormatter).isBefore(LocalDateTime.now())) {
                throw new ValidationException("Дата события не может быть меньше текущей даты", log);
            }
        if (Duration.between(LocalDateTime.now(), event.getEventDate()).toHours() < 1) {
            throw new ConflictedDataException("До начала события осталось менее 1 часа", log);
        };
        Category category = event.getCategory();
        if (request.getCategory() != null) {
            category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с ID '%d' не найдена. "
                            .formatted(request.getCategory()), log));
        }

        return EventMapper.toEventFullDto(eventRepository.save(EventMapper.updateAdminEventFields(event, request, category)));
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID '%d' не найдено. "
                        .formatted(eventId), log));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. "
                        .formatted(userId), log));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictedDataException("Пользователь не является инициатором события.", log);
        }

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictedDataException("Изменить можно только отмененные события или события в состоянии ожидания модерации.", log);
        }
        if (Duration.between(LocalDateTime.now(), event.getEventDate()).toHours() < 2) {
            throw new ConflictedDataException("До начала события осталось менее 2 часа", log);
        };
        if (request.getEventDate() != null)
            if (LocalDateTime.parse(request.getEventDate(), dateTimeFormatter).isBefore(LocalDateTime.now())) {
                throw new ValidationException("Дата события не может быть меньше текущей даты", log);
            }
        Category category = event.getCategory();
        if (request.getCategory() != null) {
            category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с ID '%d' не найдена. "
                            .formatted(request.getCategory()), log));
        }

        return EventMapper.toEventFullDto(eventRepository.save(EventMapper.updateUserEventFields(event, request, category)));
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID '%d' не найдено. "
                        .formatted(eventId), log));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. "
                        .formatted(userId), log));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictedDataException("Инициатор события не может добавить запрос на участие в своём событии", log);
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictedDataException("Нельзя участвовать в неопубликованном событии", log);
        }
        if (requestRepository.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            throw new ConflictedDataException("Достигнут лимит запросов на участие", log);
        }

        Request request = requestRepository.findByRequester_IdAndEvent_Id(userId, eventId);
        if (request != null) {
            throw new ConflictedDataException("Нельзя добавить повторный запрос", log);
        } else {
            request = new Request();
            request.setEvent(event);
            request.setRequester(user);
            if (!event.getRequestModeration())
                request.setStatus(RequestStatus.CONFIRMED);
            else
                request.setStatus(RequestStatus.PENDING);
            return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
        }
    }

    @Override
    public List<ParticipationRequestDto>  getRequestsByUser(Long userId) {

        return requestRepository.findByRequester_Id(userId).stream().map(RequestMapper::toParticipationRequestDto).toList();

    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID '%d' не найдено. "
                        .formatted(eventId), log));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. "
                        .formatted(userId), log));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictedDataException("Пользователь не является инициатором события.", log);
        }
        return requestRepository.findByEvent_Id(eventId).stream().map(RequestMapper::toParticipationRequestDto).toList();

    }

    @Override
    public EventRequestStatusUpdateResult updateRequestByUser(long userId, long eventId, EventRequestStatusUpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID '%d' не найдено. "
                        .formatted(eventId), log));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. "
                        .formatted(userId), log));
        if (!(event.getParticipantLimit() == 0 || !event.getRequestModeration())) {

            if (requestRepository.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
                throw new ConflictedDataException("Достигнут лимит запросов на участие", log);
            }
            for (Long requestId : request.getRequestIds()) {
                Request requestR = requestRepository.findById(requestId)
                        .orElseThrow(() -> new NotFoundException("Заявка с ID '%d' не найдена. "
                                .formatted(requestId), log));
                if (!requestR.getStatus().equals(RequestStatus.PENDING)) {
                    throw new ConflictedDataException("Заявка должна находиться в состоянии ожидания", log);
                }
                if (request.getStatus().equals("CONFIRMED")) {
                    if (requestRepository.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
                        requestR.setStatus(RequestStatus.REJECTED);
                    } else {
                        requestR.setStatus(RequestStatus.CONFIRMED);
                    }
                }
                if (request.getStatus().equals("REJECTED")) {
                    requestR.setStatus(RequestStatus.REJECTED);
                }
                requestRepository.save(requestR);
            }
        }
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(requestRepository.findByStatus(RequestStatus.CONFIRMED).stream().map(RequestMapper::toParticipationRequestDto).toList());
        result.setRejectedRequests(requestRepository.findByStatus(RequestStatus.REJECTED).stream().map(RequestMapper::toParticipationRequestDto).toList());
        return result;
    }

    @Override
    public ParticipationRequestDto cancelRequestByUser(long userId, long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. "
                        .formatted(userId), log));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка с ID '%d' не найдена. "
                        .formatted(requestId), log));
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

}
