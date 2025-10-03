package ru.practicum.ewm.service.comments;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.comments.dto.CommentDto;
import ru.practicum.ewm.service.comments.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto);

    @Transactional(readOnly = true)
    List<CommentDto> getCommentsByAuthor(Long userId, Integer from, Integer size);

    @Transactional(readOnly = true)
    List<CommentDto> getComments(Long eventId, Integer from, Integer size);

    @Transactional(readOnly = true)
    CommentDto getCommentById(Long commentId);

    void deleteComment(Long userId, Long commentId);

    void deleteComment(Long commentId);

}
