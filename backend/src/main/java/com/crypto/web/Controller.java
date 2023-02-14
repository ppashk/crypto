package com.crypto.web;

import com.crypto.api.*;
import com.crypto.users.security.jwt.JwtUtils;
import com.crypto.users.service.UserService;
import com.crypto.utils.annotation.CurrentUserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static com.crypto.utils.SpringUtils.buildPageable;

@SuppressWarnings({"PMD.ExcessivePublicCount"})
@Component
@Slf4j
@RequiredArgsConstructor
public class Controller implements CryptoApi {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public String generateToken(CredentialForm form) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(form.getEmail(), form.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }

    @Override
    public UserDto getUserById(@CurrentUserId String id) {
        return userService.getUserById(id);
    }

    @Override
    public FileDto getUserPhoto(String id) {
        return userService.getUserPhoto(id);
    }

    @Override
    public UserResponse getUsers(Boolean includeInactive, Integer page, Integer size, String order, String sort, String filter) {
        Pageable pageable = buildPageable(page, size, order, sort, "firstName");
        return userService.findAll(includeInactive, filter, pageable);
    }

    @Override
    public void registerUser(UserRegistrationForm form) {
        userService.registerUser(form);
    }

    @Override
    public void addUserPhoto(MultipartFile file, String id) {
        userService.addUserPhoto(file, id);
    }

    @Override
    public UserDto createUser(UserRegistrationForm form) {
        return userService.createUser(form);
    }

    @Override
    public UserDto updateUser(UserForm form, String id) {
        return userService.updateUser(id, form);
    }
}

