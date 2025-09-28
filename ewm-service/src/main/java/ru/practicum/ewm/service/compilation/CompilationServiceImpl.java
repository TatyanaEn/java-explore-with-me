package ru.practicum.ewm.service.compilation;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.categories.CategoryMapper;
import ru.practicum.ewm.service.categories.model.Category;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.service.compilation.model.Compilation;
import ru.practicum.ewm.service.compilation.model.CompilationEvent;
import ru.practicum.ewm.service.compilation.model.QCompilation;
import ru.practicum.ewm.service.compilation.repository.CompilationEventRepository;
import ru.practicum.ewm.service.compilation.repository.CompilationRepository;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.mapper.EventMapper;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.QEvent;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.exception.ConflictedDataException;
import ru.practicum.ewm.service.exception.NotFoundException;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements  CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto request) {
        if (!compilationRepository.findByTitle(request.getTitle()).isEmpty()) {
            throw new ConflictedDataException("Подборка с таким заголовком уже существует", log);
        }
        if (request.getPinned() == null)
            request.setPinned(false);
        List<EventShortDto> events = new java.util.ArrayList<>(List.of());
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilationRepository.save(ru.practicum.ewm.service.compilation.CompilationMapper.toCompilation(request)));
        if (request.getEvents() != null)
            for (Long eventId : request.getEvents()) {
                Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new NotFoundException("Событие с ID '%d' не найдено. ".formatted(eventId), log));
                CompilationEvent compilationEvent = new CompilationEvent();
                compilationEvent.setEventId(eventId);
                compilationEvent.setCompilationId(compilationDto.getId());
                compilationEventRepository.save(compilationEvent);
                events.add(EventMapper.toEventShortDto(event));

            }
        compilationDto.setEvents(events);
        return compilationDto;

    }

    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с ID '%d' не найдена. ".formatted(compId), log));
        for (CompilationEvent compilationEvent : compilationEventRepository.findByCompilationId(compId)) {
            compilationEventRepository.delete(compilationEvent);
        }
        compilationRepository.delete(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        BooleanExpression bPredicate;
        bPredicate = Expressions.asBoolean(true).isTrue();
        if (pinned != null) {
            bPredicate = bPredicate.and(QCompilation.compilation.pinned.eq(pinned));
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<CompilationDto> result = compilationRepository.findAll(bPredicate, pageable).map(CompilationMapper::toCompilationDto).getContent();
        for (CompilationDto comp : result) {
            List<EventShortDto> eventList =  new java.util.ArrayList<>(List.of());
            for (CompilationEvent compilationEvent : compilationEventRepository.findByCompilationId(comp.getId())) {
                eventList.add(EventMapper.toEventShortDto(eventRepository.findById(compilationEvent.getEventId()).get()));
            }
            comp.setEvents(eventList);
        }
        return result;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        CompilationDto result  = compilationRepository.findById(compId)
                .map(CompilationMapper::toCompilationDto)
                .orElseThrow(() -> new NotFoundException("Подборка с ID '%d' не найдена. ".formatted(compId), log));
        List<EventShortDto> eventList =  new java.util.ArrayList<>(List.of());
        for (CompilationEvent compilationEvent : compilationEventRepository.findByCompilationId(result.getId())) {
            eventList.add(EventMapper.toEventShortDto(eventRepository.findById(compilationEvent.getEventId()).get()));
        }
        result.setEvents(eventList);
        return result;
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с ID '%d' не найдена. ".formatted(compId), log));
        if (!compilationRepository.findByTitle(request.getTitle()).isEmpty()) {
            throw new ConflictedDataException("Подборка с таким заголовком уже существует", log);
        }
        for (CompilationEvent compilationEvent : compilationEventRepository.findByCompilationId(compId)) {
            compilationEventRepository.delete(compilationEvent);
        }
        CompilationDto updateCompilation = CompilationMapper.toCompilationDto(compilationRepository
                .save(CompilationMapper.updateCompilationFields(compilation, request)));
        List<EventShortDto> events = new java.util.ArrayList<>(List.of());
        if (request.getEvents() != null)
            for (Long eventId : request.getEvents()) {
                Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new NotFoundException("Событие с ID '%d' не найдено. ".formatted(eventId), log));
                CompilationEvent compilationEvent = new CompilationEvent();
                compilationEvent.setEventId(eventId);
                compilationEvent.setCompilationId(updateCompilation.getId());
                compilationEventRepository.save(compilationEvent);
                events.add(EventMapper.toEventShortDto(event));

            }
        updateCompilation.setEvents(events);
        return updateCompilation;
    }
}
