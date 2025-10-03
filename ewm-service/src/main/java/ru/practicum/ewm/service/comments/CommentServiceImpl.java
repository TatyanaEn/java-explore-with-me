package ru.practicum.ewm.service.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.comments.dto.CommentDto;
import ru.practicum.ewm.service.comments.dto.NewCommentDto;
import ru.practicum.ewm.service.comments.mapper.CommentMapper;
import ru.practicum.ewm.service.comments.model.Comment;
import ru.practicum.ewm.service.comments.repository.CommentRepository;
import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.event.mapper.EventMapper;
import ru.practicum.ewm.service.event.model.*;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.repository.RequestRepository;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.service.exception.ValidationException;
import ru.practicum.ewm.service.user.UserMapper;
import ru.practicum.ewm.service.user.UserRepository;
import ru.practicum.ewm.service.user.dto.UserShortDto;
import ru.practicum.ewm.service.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.service.event.model.EventState.PUBLISHED;
import static ru.practicum.ewm.service.event.model.RequestStatus.CONFIRMED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User author = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);
        if (event.getState() != PUBLISHED) {
            throw new ValidationException("Комментировать можно только опубликованные события.", log);
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(newCommentDto, author, event));
        UserShortDto userShort = UserMapper.toUserShortDto(author);
        EventShortDto eventShort = EventMapper.toEventShortDto(event);
        eventShort.setConfirmedRequests(requestRepository.countByEvent_IdAndStatus(eventId, CONFIRMED));
        return CommentMapper.toCommentDto(comment, userShort, eventShort);
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        User author = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с ID '%d' не найдено. ".formatted(commentId), log));
        if (comment.getEvent() != event) {
            throw new ValidationException("Комментарий не этого события.", log);
        }
        comment.setText(newCommentDto.getText());
        comment.setEdited(LocalDateTime.now());
        UserShortDto userShort = UserMapper.toUserShortDto(author);
        EventShortDto eventShort = EventMapper.toEventShortDto(event);
        eventShort.setConfirmedRequests(requestRepository.countByEvent_IdAndStatus(eventId, CONFIRMED));
        return CommentMapper.toCommentDto(comment, userShort, eventShort);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getCommentsByAuthor(Long userId, Integer from, Integer size) {
        User author = checkAndGetUser(userId);
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, PageRequest.of(from / size, size));
        UserShortDto userShort = UserMapper.toUserShortDto(author);
        return comments.stream().map(c -> {
                    EventShortDto eventShort = EventMapper.toEventShortDto(c.getEvent());
                    eventShort.setConfirmedRequests(requestRepository.countByEvent_IdAndStatus(c.getEvent().getId(), CONFIRMED));
                    return CommentMapper.toCommentDto(c, userShort, eventShort);
            }
        ).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getComments(Long eventId, Integer from, Integer size) {
        Event event = checkAndGetEvent(eventId);
        EventShortDto eventShort = EventMapper.toEventShortDto(event);
        eventShort.setConfirmedRequests(requestRepository.countByEvent_IdAndStatus(eventId, CONFIRMED));
        return commentRepository.findAllByEventId(eventId, PageRequest.of(from / size, size))
                .stream()
                .map(c -> CommentMapper.toCommentDto(c, UserMapper.toUserShortDto(c.getAuthor()), eventShort))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = checkAndGetComment(commentId);
        UserShortDto userShort = UserMapper.toUserShortDto(comment.getAuthor());
        EventShortDto eventShort = EventMapper.toEventShortDto(comment.getEvent());
        eventShort.setConfirmedRequests(requestRepository.countByEvent_IdAndStatus(comment.getEvent().getId(), CONFIRMED));
        return CommentMapper.toCommentDto(comment, userShort, eventShort);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        User author = checkAndGetUser(userId);
        Comment comment = checkAndGetComment(commentId);
        if (comment.getAuthor() != author) {
            throw new ValidationException("Только автор может удалять комментарий", log);
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteComment(Long commentId) {
        checkAndGetComment(commentId);
        commentRepository.deleteById(commentId);
    }

    private User checkAndGetUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID '%d' не найден. "
                        .formatted(userId), CommentServiceImpl.log));
    }

    private Event checkAndGetEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с ID '%d' не найдено. ".formatted(eventId), CommentServiceImpl.log));
    }

    private Comment checkAndGetComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с ID '%d' не найдено. ".formatted(commentId), CommentServiceImpl.log));
    }
}
