package ru.practicum.ewm.service.comments.mapper;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.service.comments.dto.CommentDto;
import ru.practicum.ewm.service.comments.dto.NewCommentDto;
import ru.practicum.ewm.service.comments.model.Comment;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.user.dto.UserShortDto;
import ru.practicum.ewm.service.user.model.User;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    public static Comment toComment(NewCommentDto newCommentDto, User author, Event event) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setText(newCommentDto.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment, UserShortDto author, EventShortDto event) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                author,
                event,
                comment.getCreated(),
                comment.getEdited()
        );
    }
}