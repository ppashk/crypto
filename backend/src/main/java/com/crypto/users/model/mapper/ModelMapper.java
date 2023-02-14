package com.crypto.users.model.mapper;

import com.crypto.api.UserDto;
import com.crypto.api.UserResponse;
import com.crypto.api.UserShortDto;
import com.crypto.users.model.User;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class ModelMapper {

    public static UserResponse toUserResponse(Page<User> page) {
        return UserResponse.builder()
                .total(page.getTotalElements())
                .items(page.getContent()
                        .stream()
                        .map(ModelMapper::toUserShort)
                        .collect(toList()))
                .build();
    }

    public static UserShortDto toUserShort(User entity) {
        return UserShortDto.builder()
                .email(entity.getEmail())
                .lastName(entity.getLastName())
                .firstName(entity.getFirstName())
                .active(entity.isActive())
                .build();
    }

    public static UserDto toUserDto(User entity) {
        return UserDto.builder()
                .id(entity.getId().toString())
                .email(entity.getEmail())
                .lastName(entity.getLastName())
                .firstName(entity.getFirstName())
                .active(entity.isActive())
                .createdTime(entity.getCreatedTime())
                .updatedTime(entity.getUpdatedTime())
                .roles(entity.getRoles())
                .build();
    }
}
