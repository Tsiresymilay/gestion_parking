package com.example.parking.service.impl;

import com.example.parking.dto.request.UserRequest;
import com.example.parking.dto.response.UserResponse;
import com.example.parking.entity.AppUser;
import com.example.parking.entity.Role;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.mapper.UserMapper;
import com.example.parking.repository.AppUserRepository;
import com.example.parking.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Le nom d'utilisateur " + request.getUsername() + " est deja utilise");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("L'email " + request.getEmail() + " est deja utilise");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new com.example.parking.exception.BusinessException("Le mot de passe est obligatoire");
        }
        AppUser user = new AppUser();
        apply(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        AppUser user = getEntity(id);
        if (userRepository.existsByUsernameAndIdNot(request.getUsername(), id)) {
            throw new ConflictException("Le nom d'utilisateur " + request.getUsername() + " est deja utilise");
        }
        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new ConflictException("L'email " + request.getEmail() + " est deja utilise");
        }
        apply(request, user);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return UserMapper.toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public AppUser getEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Utilisateur", id));
    }

    @Override
    @Transactional(readOnly = true)
    public AppUser getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> ResourceNotFoundException.of("Utilisateur", username));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> search(String keyword, Role role, Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return userRepository.search(kw, role, pageable).map(UserMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(UserMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(getEntity(id));
    }

    private void apply(UserRequest request, AppUser user) {
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setUserType(request.getUserType());
        user.setEnabled(request.isEnabled());
    }
}
