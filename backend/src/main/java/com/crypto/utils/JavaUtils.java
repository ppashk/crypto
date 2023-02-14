package com.crypto.utils;

import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Long.parseLong;
import static java.util.Optional.ofNullable;

@UtilityClass
public class JavaUtils {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)?[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String EMPTY = "";

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidMailAddress(final String hex) {
        if (isBlank(hex)) {
            return Boolean.FALSE;
        }
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(hex.trim());
        return matcher.matches();
    }

    public static String emptyIfNull(String input) {
        return null == input ? EMPTY : input;
    }

    public static String parseStringSafe(Object value) {
        if (null == value) {
            return null;
        }
        return String.valueOf(value);
    }

    public static Optional<String> string(String string) {
        return string(string, String::trim);
    }

    public static Optional<String> string(String string, Function<String, String> transformer) {
        if (string != null) {
            string = transformer.apply(string);
            if (!string.isEmpty()) {
                return Optional.of(string);
            }
        }
        return Optional.empty();
    }

    public static <T, R> R getOrNull(T object, Function<T, R> function) {
        return getOrDefault(object, function, null);
    }

    public static <T, U> U getOrDefault(T object, Function<T, U> function, U defaultValue) {
        return ofNullable(object)
                .map(function)
                .orElse(defaultValue);
    }

    public static String getFileExtension(String name) {
        try {
            return name.substring(name.lastIndexOf('.') + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFileNameWithoutExtension(String name) {
        try {
            return name.substring(0, name.lastIndexOf('.'));
        } catch (Exception e) {
            return "";
        }
    }

    public static Long parseLongSafe(String value) {
        if (value == null) {
            return null;
        }
        try {
            return parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
