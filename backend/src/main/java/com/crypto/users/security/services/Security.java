package com.crypto.users.security.services;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class Security {

    public static final String ADMIN = "ROLE_ADMIN";

    public static boolean isAdmin() {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getAuthorities().stream().anyMatch(a -> ADMIN.equals(a.getAuthority()));
    }

    public static Long currentUserId() {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getId();
    }
}
