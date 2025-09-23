package ru.practicum.ewm.service.user;


import ru.practicum.ewm.service.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> userIds, Integer from, Integer size);

    UserDto createUser(UserDto request);


    void deleteUser(Long userId);
}
