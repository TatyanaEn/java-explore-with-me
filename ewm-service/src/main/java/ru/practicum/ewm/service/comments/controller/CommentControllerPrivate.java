package ru.practicum.ewm.service.comments.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.service.comments.CommentService;
import ru.practicum.ewm.service.comments.dto.CommentDto;
import ru.practicum.ewm.service.comments.dto.NewCommentDto;

import java.util.List;

@Validated
@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class CommentControllerPrivate {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable("userId")  Long userId,
                                 @PathVariable("eventId")  Long eventId,
                                 @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{eventId}/{commentId}")
    public CommentDto updateComment(@PathVariable("userId") Long userId,
                                    @PathVariable("eventId") Long eventId,
                                    @PathVariable("commentId") Long commentId,
                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.updateComment(userId, eventId, commentId, newCommentDto);
    }

    @GetMapping
    List<CommentDto> getCommentsByAuthor(@PathVariable("userId") Long userId,
                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return commentService.getCommentsByAuthor(userId, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("userId") Long userId,
                              @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(userId, commentId);
    }
}