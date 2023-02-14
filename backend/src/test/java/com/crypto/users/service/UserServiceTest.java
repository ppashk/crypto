package com.crypto.users.service;

import com.crypto.api.UserForm;
import com.crypto.api.UserRegistrationForm;
import com.crypto.users.model.User;
import com.crypto.users.repository.UserRepository;
import com.crypto.users.security.services.Security;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

import static com.crypto.api.I18N.*;
import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final Long ID = 12313123123L;
    private static final String PASSWORD = RandomStringUtils.random(10, true, false) + RandomUtils.nextInt();
    private static final String FIRST_NAME = RandomStringUtils.random(10, true, false);
    private static final String LAST_NAME = RandomStringUtils.random(10, true, false);
    private static final String EMAIL = "good.email@gmail.com";

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    public void should_build_user_on_create_when_everything_is_correct() {
        UserRegistrationForm form = initUserRegistrationForm();
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User user = (User) invocation.getArguments()[0];
            user.setId(ID);
            return user;
        });

        userService.createUser(form);

        verify(userRepository).save(userArgumentCaptor.capture());

        User user = userArgumentCaptor.getValue();

        assertTrue(user.isActive());
        assertEquals(user.getEmail(), form.getEmail());
        assertEquals(user.getFirstName(), form.getFirstName());
        assertEquals(user.getLastName(), form.getLastName());

        verify(userRepository).save(any());
        verify(passwordEncoder).encode(any());
        verify(userRepository).findByEmail(any());
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

//    @Test
//    public void should_build_user_on_update_when_everything_is_correct() {
//        try (MockedStatic<Security> utilities = Mockito.mockStatic(Security.class)) {
//            UserForm form = initUpdatedUserForm();
//            form.setOldPassword(PASSWORD);
//            User initUser = initUser();
//            when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
//            when(userRepository.findById(ID)).thenReturn(Optional.of(initUser));
//            utilities.when(Security::currentUserId).thenReturn(ID);
//
//            userService.updateUser(valueOf(ID), form);
//
//            verify(userRepository).save(userArgumentCaptor.capture());
//
//            User user = userArgumentCaptor.getValue();
//
//            assertTrue(user.isActive());
//            assertEquals(user.getEmail(), form.getEmail());
//            assertEquals(user.getFirstName(), form.getFirstName());
//            assertEquals(user.getLastName(), form.getLastName());
//
//            verify(userRepository).save(any());
//            verify(userRepository).findById(any());
//            verify(userRepository).findByEmail(any());
//            verifyNoMoreInteractions(userRepository, passwordEncoder);
//        }
//    }

    @Test
    public void should_throw_exception_on_create_when_email_is_already_exist() {
        UserRegistrationForm form = initUserRegistrationForm();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(new User()));

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> userService.createUser(form));
        assertNotNull(thrown.getMessage());
        assertTrue(thrown.getMessage().contains(EMAIL_ALREADY_EXISTS.toString()));

        verify(userRepository).findByEmail(any());
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    public void should_throw_exception_on_update_when_email_is_already_exist() {
        try (MockedStatic<Security> utilities = Mockito.mockStatic(Security.class)) {
            UserForm form = initUpdatedUserForm();
            User user = initUser();
            User oldUser = initUser();
            oldUser.setEmail("updateduser@test.com");
            when(userRepository.findById(ID)).thenReturn(Optional.of(user));
            when(userRepository.findByEmail(form.getEmail())).thenReturn(Optional.of(oldUser));
            utilities.when(Security::currentUserId).thenReturn(ID);

            HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () ->
                    userService.updateUser(valueOf(ID), form));
            assertNotNull(thrown.getMessage());
            assertTrue(thrown.getMessage().contains(EMAIL_ALREADY_EXISTS.toString()));

            verify(userRepository).findById(any());
            verify(userRepository).findByEmail(any());
            verifyNoMoreInteractions(userRepository, passwordEncoder);
        }
    }

    @Test
    public void should_throw_exception_on_create_when_email_is_wrong() {
        UserRegistrationForm form = initUserRegistrationForm();
        form.setEmail("wrong");

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> userService.createUser(form));
        assertNotNull(thrown.getMessage());
        assertTrue(thrown.getMessage().contains(WRONG_FORMAT_OF_EMAIL.toString()));

        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    public void should_throw_exception_on_update_when_email_is_wrong() {
        try (MockedStatic<Security> utilities = Mockito.mockStatic(Security.class)) {
            UserForm form = initUpdatedUserForm();
            form.setEmail("wrong");
            User user = initUser();
            when(userRepository.findById(ID)).thenReturn(Optional.of(user));
            utilities.when(Security::currentUserId).thenReturn(ID);

            HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () ->
                    userService.updateUser(valueOf(ID), form));
            assertNotNull(thrown.getMessage());
            assertTrue(thrown.getMessage().contains(WRONG_FORMAT_OF_EMAIL.toString()));

            verify(userRepository).findById(any());
            verifyNoMoreInteractions(userRepository, passwordEncoder);
        }
    }

    @Test
    public void should_throw_exception_on_create_when_password_is_wrong() {
        UserRegistrationForm form = initUserRegistrationForm();
        form.setPassword("wrong");

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> userService.createUser(form));
        assertNotNull(thrown.getMessage());
        assertTrue(thrown.getMessage().contains(WRONG_FORMAT_OF_PASSWORD.toString()));

        verify(userRepository).findByEmail(any());
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    public void should_throw_exception_on_update_when_password_is_wrong() {
        try (MockedStatic<Security> utilities = Mockito.mockStatic(Security.class)) {
            UserForm form = initUpdatedUserForm();
            form.setPassword("wrong");
            form.setRepeatPassword("wrong");
            form.setOldPassword(PASSWORD);
            User user = initUser();
            when(userRepository.findById(ID)).thenReturn(Optional.of(user));
            utilities.when(Security::currentUserId).thenReturn(ID);

            HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () ->
                    userService.updateUser(valueOf(ID), form));
            assertNotNull(thrown.getMessage());
            assertTrue(thrown.getMessage().contains(WRONG_PASSWORD.toString()));

            verify(userRepository).findById(any());
            verify(userRepository).findByEmail(any());
            verify(passwordEncoder).matches(any(), any());
            verifyNoMoreInteractions(userRepository, passwordEncoder);
        }
    }

    @Test
    public void should_throw_exception_on_create_when_email_is_empty() {
        UserRegistrationForm form = initUserRegistrationForm();
        form.setEmail("");

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> userService.createUser(form));
        assertNotNull(thrown.getMessage());
        assertTrue(thrown.getMessage().contains(WRONG_FORMAT_OF_EMAIL.toString()));

        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    public void should_throw_exception_on_update_when_email_is_empty() {
        try (MockedStatic<Security> utilities = Mockito.mockStatic(Security.class)) {
            UserForm form = initUpdatedUserForm();
            form.setEmail("");
            User user = initUser();
            when(userRepository.findById(ID)).thenReturn(Optional.of(user));
            utilities.when(Security::currentUserId).thenReturn(ID);

            HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () ->
                    userService.updateUser(valueOf(ID), form));
            assertNotNull(thrown.getMessage());
            assertTrue(thrown.getMessage().contains(WRONG_FORMAT_OF_EMAIL.toString()));

            verifyNoMoreInteractions(userRepository, passwordEncoder);
        }
    }

    @Test
    public void should_throw_exception_on_create_when_password_is_empty() {
        UserRegistrationForm form = initUserRegistrationForm();
        form.setPassword("");

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> userService.createUser(form));
        assertNotNull(thrown.getMessage());
        assertTrue(thrown.getMessage().contains(PASSWORD_REQUIRED.toString()));

        verify(userRepository).findByEmail(any());
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    public void should_throw_exception_on_create_when_first_name_is_empty() {
        UserRegistrationForm form = initUserRegistrationForm();
        form.setFirstName("");

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> userService.createUser(form));
        assertNotNull(thrown.getMessage());
        assertTrue(thrown.getMessage().contains(FIRSTNAME_REQUIRED.toString()));

        verify(userRepository).findByEmail(any());
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    private User initUser() {
        User user = new User();
        user.setId(ID);
        user.setActive(true);
        user.setEmail(EMAIL);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        return user;
    }

    private UserRegistrationForm initUserRegistrationForm() {
        UserRegistrationForm form = new UserRegistrationForm();
        form.setEmail(EMAIL);
        form.setFirstName(FIRST_NAME);
        form.setLastName(LAST_NAME);
        form.setPassword(PASSWORD);
        form.setRepeatPassword(PASSWORD);
        return form;
    }

    private UserForm initUpdatedUserForm() {
        UserForm form = new UserForm();
        form.setEmail("updateduser@test.com");
        form.setFirstName("Updated");
        form.setLastName("Test");
        form.setPassword("123poiu");
        form.setRepeatPassword("123poiu");
        return form;
    }
}
