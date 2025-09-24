package ru.practicum.ewm.service.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.user.dto.UserDto;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(value = "ids", required = false) List<Long> ids,
                                  @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userRequest) {
        return userService.createUser(userRequest);
    }


    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }
}
