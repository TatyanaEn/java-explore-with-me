package ru.practicum.ewm.service.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.model.QEvent;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.service.user.UserMapper;
import ru.practicum.ewm.service.user.UserRepository;
import ru.practicum.ewm.service.user.UserService;
import ru.practicum.ewm.service.user.dto.UserDto;
import ru.practicum.ewm.service.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;


    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                                         Boolean onlyAvailable, String sort, Integer from, Integer size){
        Pageable pageable = PageRequest.of(from / size, size);
        BooleanExpression bText = QEvent.event.annotation.likeIgnoreCase(text);
        eventRepository.findAll(, pageable);

    }




}
