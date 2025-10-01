package ru.practicum.ewm.service.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.user.dto.UserShortDto;
import java.time.LocalDateTime;
import static ru.practicum.ewm.service.util.DateConstant.DATE_TIME_PATTERN;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;
    private String text;
    private UserShortDto author;
    private EventShortDto event;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime created;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime edited;
}