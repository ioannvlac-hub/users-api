package com.example.users_manager.controller;

import com.example.users_manager.dto.*;
import com.example.users_manager.entity.enums.Gender;
import com.example.users_manager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserResponseDto createUser(@Valid @RequestBody CreateUpdateUserRequestDto dto) {

        Long id = userService.createUser(dto);

        return CreateUserResponseDto.builder()
                .id(id)
                .message("User created successfully")
                .build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDetailsResponseDto getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DeleteUserResponseDto deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return DeleteUserResponseDto.builder()
                .message("User deleted successfully")
                .build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PagedResponse<UserListItemDto> listUsers(
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.listUsers(gender, search, page, size);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody CreateUpdateUserRequestDto dto
    ) {

        userService.updateUser(id, dto);

        return Map.of("message", "User updated successfully");
    }
}