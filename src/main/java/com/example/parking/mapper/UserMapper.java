package com.example.parking.mapper;

import com.example.parking.dto.response.UserResponse;
import com.example.parking.entity.AppUser;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(AppUser user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setUserType(user.getUserType());
        response.setEnabled(user.isEnabled());
        return response;
    }
}
