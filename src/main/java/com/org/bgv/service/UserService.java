package com.org.bgv.service;

import com.org.bgv.api.response.ApiResponse;
import com.org.bgv.dto.UserDto;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.mapper.UserMapper;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.UserRoleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper; // Inject mapper bean
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public ApiResponse<List<UserDto>> getAll() {
        List<UserDto> dtos = userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return ApiResponse.success("Fetched users successfully", dtos, HttpStatus.OK);
    }

    public ApiResponse<UserDto> getById(Long id) {
        return userRepository.findById(id)
                .map(user -> ApiResponse.success("User found", userMapper.toDto(user), HttpStatus.OK))
                .orElse(ApiResponse.failure("User not found", HttpStatus.NOT_FOUND));
    }

    public ApiResponse<UserDto> create(UserDto userDto) {
        User saved = userRepository.save(userMapper.toEntity(userDto));
        return ApiResponse.success("User created", userMapper.toDto(saved), HttpStatus.CREATED);
    }

    public ApiResponse<UserDto> update(Long id, UserDto dto) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(dto.getFirstName());
                    existing.setLastName(dto.getLastName());
                    existing.setEmail(dto.getEmail());
                    User saved = userRepository.save(existing);
                    return ApiResponse.success("User updated", userMapper.toDto(saved), HttpStatus.OK);
                }).orElse(ApiResponse.failure("User not found", HttpStatus.NOT_FOUND));
    }

    public ApiResponse<Void> delete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ApiResponse.success("User deleted", null, HttpStatus.OK);
        }
        return ApiResponse.failure("User not found", HttpStatus.NOT_FOUND);
    }
    
    @Transactional
    public void assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role role = roleRepository.findByName(roleName).orElseThrow(()-> new RuntimeException("Role not found"));

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        user.getRoles().add(userRole); // maintain bidirectional consistency
        userRoleRepository.save(userRole); // persist relation
    }
}
