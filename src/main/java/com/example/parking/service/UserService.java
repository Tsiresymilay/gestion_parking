package com.example.parking.service;

import com.example.parking.dto.request.UserRequest;
import com.example.parking.dto.response.UserResponse;
import com.example.parking.entity.AppUser;
import com.example.parking.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserResponse create(UserRequest request);

    UserResponse update(Long id, UserRequest request);

    UserResponse findById(Long id);

    AppUser getEntity(Long id);

    AppUser getByUsername(String username);

    Page<UserResponse> search(String keyword, Role role, Pageable pageable);

    List<UserResponse> findAll();

    void delete(Long id);
}
