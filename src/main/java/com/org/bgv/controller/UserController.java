package com.org.bgv.controller;

import com.org.bgv.api.response.ApiResponse;
import com.org.bgv.dto.UserDto;
import com.org.bgv.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<List<UserDto>> getAll() { return userService.getAll(); }

    @GetMapping("/{id}")
    public ApiResponse<UserDto> getById(@PathVariable Long id) { return userService.getById(id); }

    @PostMapping
    public ApiResponse<UserDto> create(@RequestBody UserDto userDto) { return userService.create(userDto); }

    @PutMapping("/{id}")
    public ApiResponse<UserDto> update(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) { return userService.delete(id); }
}
