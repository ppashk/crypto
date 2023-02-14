package com.crypto.users.service;

import com.crypto.api.*;
import com.crypto.common.service.FileService;
import com.crypto.users.model.User;
import com.crypto.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.regex.Pattern;

import static com.crypto.api.I18N.*;
import static com.crypto.api.UserRole.ADMIN;
import static com.crypto.api.UserRole.USER;
import static com.crypto.users.model.mapper.ModelMapper.toUserDto;
import static com.crypto.users.model.mapper.ModelMapper.toUserResponse;
import static com.crypto.users.security.services.Security.currentUserId;
import static com.crypto.users.security.services.Security.isAdmin;
import static com.crypto.utils.JavaUtils.*;
import static com.crypto.utils.SpringUtils.checkArgumentCustom;
import static java.lang.Long.parseLong;
import static java.util.Set.of;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(?=.*[0-9])(?=.*[a-zA-Z]).{6,64}");
    private static final Random RANDOM = new Random();
    private static final String REQUIRED_PHOTO_EXTENSION = "png";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    @Value("${photo.directory}")
    private String photoDirectory;

    public void registerUser(UserRegistrationForm form) {
        validateUserOnCreate(form);
        User user = mapUser(form);
        save(user);
    }

    public UserDto createUser(UserRegistrationForm form) {
        validateUserOnCreate(form);
        User user = mapUser(form);
        if (falseIfNull(form.getIsAdmin())) {
            user.getRoles().add(ADMIN);
        }
        return toUserDto(save(user));
    }

    public UserDto updateUser(String id, UserForm form) {
        User user = findById(parseLong(id));
        updateUserFields(form, user);
        return toUserDto(save(user));
    }

    public UserResponse findAll(Boolean includeInactive, String filter, Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(includeInactive, emptyIfNull(filter), pageable);
        return toUserResponse(usersPage);
    }

    public UserDto getUserById(String id) {
        User user = findById(parseLongSafe(id));
        checkArgumentCustom(user.getId().equals(currentUserId()) || isAdmin(), NO_AUTHORITIES_TO_VIEW_THIS_USER);
        return toUserDto(user);
    }

    @SneakyThrows
    public void addUserPhoto(MultipartFile file, String id) {
        validatePhoto(file, id);
        User user = findById(parseLong(id));
        user.setPhoto(generateReference(file.getName()));
        fileService.uploadFile(photoDirectory, user.getPhoto(), file.getBytes());
        userRepository.save(user);
    }

    @SneakyThrows
    public FileDto getUserPhoto(String id) {
        User user = findById(parseLong(id));
        checkArgumentCustom(isNotBlank(user.getPhoto()), NO_USER_PHOTO);
        byte[] photo = fileService.downloadFile(photoDirectory, user.getPhoto());
        return FileDto.builder()
                .content(photo)
                .name(user.getFirstName() + ".png")
                .build();
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, parseStringSafe(id)));
    }

    private void validateUserOnCreate(UserRegistrationForm form) {
        checkArgumentCustom(isNotBlank(form.getEmail()) && isValidMailAddress(form.getEmail()), WRONG_FORMAT_OF_EMAIL);
        checkArgumentCustom(isEmailNotRegistered(form.getEmail()), EMAIL_ALREADY_EXISTS);
        checkArgumentCustom(isNotBlank(form.getPassword()), PASSWORD_REQUIRED);
        checkArgumentCustom(PASSWORD_PATTERN.matcher(form.getPassword()).matches(), WRONG_FORMAT_OF_PASSWORD);
        checkArgumentCustom(form.getPassword().equals(form.getRepeatPassword()), PASSWORD_DOES_NOT_MATCH);
        checkArgumentCustom(validName(form.getFirstName()), FIRSTNAME_REQUIRED);
        checkArgumentCustom(validName(form.getLastName()), LASTNAME_REQUIRED);
    }

    private void validatePhoto(MultipartFile file, String id) {
        checkArgumentCustom(currentUserId().equals(parseLong(id)), NO_AUTHORITIES_TO_CHANGE_THIS_USER);
        checkArgumentCustom(REQUIRED_PHOTO_EXTENSION.equals(getFileExtension(file.getOriginalFilename())),
                PHOTO_EXTENSION_MUST_BE_PNG);
    }

    private boolean isEmailNotRegistered(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    private static boolean validName(String name) {
        return isNotBlank(name) && name.length() > 1 && name.length() < 65;
    }

    private User mapUser(UserRegistrationForm form) {
        User user = new User();
        user.setEmail(trimToNull(form.getEmail()));
        user.setFirstName(trimToNull(form.getFirstName()));
        user.setLastName(trimToNull(form.getLastName()));
        user.setPassword(passwordEncoder.encode(trimToNull(form.getPassword())));
        user.setActive(true);
        user.setRoles(of(USER));
        return user;
    }

    private void updateUserFields(UserForm form, User user) {
        checkArgumentCustom(user.getId().equals(currentUserId()) || isAdmin(), NO_AUTHORITIES_TO_CHANGE_THIS_USER);
        checkArgumentCustom(isNotBlank(form.getEmail()) && isValidMailAddress(form.getEmail()), WRONG_FORMAT_OF_EMAIL);
        if (!form.getEmail().equalsIgnoreCase(user.getEmail())) {
            checkArgumentCustom(isEmailNotRegistered(form.getEmail()), EMAIL_ALREADY_EXISTS);
            user.setEmail(trimToNull(form.getEmail()));
        }
        if (form.getPassword() != null && form.getOldPassword() != null) {
            checkArgumentCustom(passwordEncoder.matches(form.getOldPassword(), user.getPassword()), WRONG_PASSWORD);
            checkArgumentCustom(PASSWORD_PATTERN.matcher(form.getPassword()).matches(), WRONG_FORMAT_OF_PASSWORD);
            checkArgumentCustom(form.getPassword().equals(form.getRepeatPassword()), PASSWORD_DOES_NOT_MATCH);
            user.setPassword(passwordEncoder.encode(trimToNull(form.getPassword())));
        }
        if (form.getFirstName() != null) {
            user.setFirstName(trimToNull(form.getFirstName()));
        }
        if (form.getLastName() != null) {
            user.setLastName(trimToNull(form.getLastName()));
        }
    }

    private User save(User user) {
        return userRepository.save(user);
    }

    private String generateReference(String fileName) {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                + "/"
                + RANDOM.nextLong()
                + getFileExtension(fileName);
    }
}
