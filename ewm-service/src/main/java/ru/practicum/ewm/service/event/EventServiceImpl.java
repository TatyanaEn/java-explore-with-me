package ru.practicum.ewm.service.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    final StatsClient statsClient;
    @Value("${app}")
    String app;


    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateConstant.DATE_TIME_PATTERN)
            .withZone(ZoneOffset.UTC);

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,String rangeStart, String rangeEnd,
                                                Integer from, Integer size){
        BooleanExpression bPredicate;
        bPredicate = Expressions.asBoolean(true).isTrue();

        List<EventState> enumList = new ArrayList<>();
        if (states != null) {
            for (String str : states) {
                EventState myEnum = EventState.valueOf(str);
                enumList.add(myEnum);
            }
            bPredicate = bPredicate.and(QEvent.event.state.in(enumList));
        }


        if (users != null) {
            bPredicate = bPredicate.and(QEvent.event.initiator.id.in(users));
        }
        if (categories != null) {
            bPredicate = bPredicate.and(QEvent.event.category.id.in(categories));
        }
        if (rangeStart == null || rangeStart.isBlank()) {
            rangeStart = dateTimeFormatter
                    .format(LocalDateTime.now());
        }
        if (!(rangeStart == null || rangeStart.isBlank())) {
            bPredicate = bPredicate.and(QEvent.event.eventDate.after(LocalDateTime.parse(rangeStart, dateTimeFormatter)));
        }

        if (!(rangeEnd == null || rangeEnd.isBlank())) {
            bPredicate = bPredicate.and(QEvent.event.eventDate.before(LocalDateTime.parse(rangeEnd, dateTimeFormatter)));
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("eventDate").descending());

        return eventRepository.findAll(bPredicate, pageable).stream().map(EventMapper::toEventFullDto)
                .peek(event ->
                        event.setConfirmedRequests(requestRepository.countByEvent_IdAndStatus(event.getId(),
                                RequestStatus.CONFIRMED)))
                .toList();

    }

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        BooleanExpression bPredicate;
        bPredicate = QEvent.event.initiator.id.eq(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAll(bPredicate, pageable).stream()
                .map(EventMapper::toEventShortDto)
                .peek(event ->
                    event.setConfirmedRequests(requestRepository.countByEvent_IdAndStatus(event.getId(),
                        RequestStatus.CONFIRMED)))
                .toList();

    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,String rangeStart,
                                         String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size,
                                         HttpServletRequest request){

        BooleanExpression bPredicate = Expressions.asBoolean(true).isTrue();

        bPredicate = bPredicate.and(QEvent.event.state.eq(EventState.PUBLISHED));
        if (!(text == null || text.isBlank())) {
            BooleanExpression bText = QEvent.event.annotation.likeIgnoreCase(text).or(ru.practicum.ewm.service.event.model.QEvent.event.description.likeIgnoreCase(text));
            bPredicate = bPredicate.and(bText);
        }
        if (categories != null) {
            bPredicate = bPredicate.and(QEvent.event.category.id.in(categories));
        }
        if (paid != null) {
            bPredicate = bPredicate.and(QEvent.event.paid.eq(paid));
        }
        if (rangeStart == null || rangeStart.isBlank()) {
            rangeStart = dateTimeFormatter
                    .format(LocalDateTime.now());
        }
        if (!(rangeStart == null || rangeStart.isBlank())) {
            bPredicate = bPredicate.and(QEvent.event.eventDate.after(LocalDateTime.parse(rangeStart, dateTimeFormatter)));
        }

        if (!(rangeEnd == null || rangeEnd.isBlank())) {
            if (LocalDateTime.parse(rangeStart, dateTimeFormatter).isAfter(LocalDateTime.parse(rangeEnd, dateTimeFormatter)))
                throw new ValidationException("Не верно указан временной диапазон", log);
            bPredicate = bPredicate.and(QEvent.event.eventDate.before(LocalDateTime.parse(rangeEnd, dateTimeFormatter)));
        }
        Pageable pageable = null;
        if (sort != null) {
            Sort sortOrder = null;
            if (Objects.equals(sort, "EVENT_DATE")) {
                sortOrder = Sort.by("eventDate").ascending();
            }
            pageable = PageRequest.of(from / size, size, sortOrder);
        } else {
            pageable = PageRequest.of(from / size, size);
        }
        List<Event> result = eventRepository.findAll(bPredicate, pageable)
                .stream().toList();

        if (onlyAvailable != null) {
            result.stream()
                    .filter(event -> {
                            Long cntConfirmedRequest = requestRepository.countByEvent_IdAndStatus(event.getId(),
                                RequestStatus.CONFIRMED);
                            return event.getParticipantLimit() > cntConfirmedRequest;
                    }).toList();
        }
        Optional<LocalDateTime> start = result.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo);
        HitDto hit = new HitDto(app, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        statsClient.saveHit(hit);
        List<EventShortDto> resultDto = result.stream().map(EventMapper::toEventShortDto)
                .peek(event -> {
                            event.setConfirmedRequests(requestRepository.countByEvent_IdAndStatus(event.getId(),
                                    RequestStatus.CONFIRMED));
                            List<String> uris = List.of(String.format("/events/%s", event.getId()));
                            event.setViews(getViewsCount(start.get(), LocalDateTime.now(), uris, true));
                        }
                ).toList();
        if (sort != null)
            if (Objects.equals(sort, "VIEWS")) {
                resultDto = resultDto.stream().sorted(Comparator.comparingLong(EventShortDto::getViews)).toList();
            }
        return resultDto;

    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID '%d' не найдено. ".formatted(eventId), log));
        if (event.getState().equals(EventState.PUBLISHED)) {
            EventFullDto result =EventMapper.toEventFullDto(event);
            result.setConfirmedRequests(requestRepository.countByEvent_IdAndStatus(event.getId(),
                    RequestStatus.CONFIRMED));
            result.setViews(getViewsCount(event.getCreatedOn(), LocalDateTime.now(), List.of(request.getRequestURI()), true));

            HitDto hit = new HitDto(app, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
            statsClient.saveHit(hit);
            return result;
        }
        else
            throw new NotFoundException("Событие не опубликовано", log);
    }

    private Long getViewsCount(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        ResponseEntity<Object> response = statsClient.getStats(start, end, uris, unique);
        ObjectMapper mapper = new ObjectMapper();
        List<ViewStatsDto> statsDto = mapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        if (!statsDto.isEmpty()) {
            return statsDto.get(0).getHits();
        } else return 0L;
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
        if (event.getParticipantLimit() > 0 )
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
            if (event.getParticipantLimit() > 0 ) {
                if (!event.getRequestModeration())
                    request.setStatus(RequestStatus.CONFIRMED);
                else
                    request.setStatus(RequestStatus.PENDING);
            } else
                request.setStatus(RequestStatus.CONFIRMED);
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
        result.setConfirmedRequests(requestRepository.findByIdInAndStatus(request.getRequestIds() ,RequestStatus.CONFIRMED).stream().map(RequestMapper::toParticipationRequestDto).toList());
        result.setRejectedRequests(requestRepository.findByIdInAndStatus(request.getRequestIds(), RequestStatus.REJECTED).stream().map(RequestMapper::toParticipationRequestDto).toList());
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
