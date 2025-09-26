package ru.practicum.ewm.service.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.user.dto.UserShortDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private String created;

    private Long event;

    private Long id;

    private Long requester;

    private String status;

}
