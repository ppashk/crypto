package com.crypto.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.client.HttpClientErrorException;

import static com.crypto.utils.JavaUtils.getOrNull;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@UtilityClass
public class SpringUtils {
    
    public static Pageable buildPageable(Integer page, Integer size, String sortDirection, String sortProperties, String defaultSort) {
        return PageRequest.of(
                ofNullable(page).orElse(0),
                ofNullable(size).orElse(1000),
                JavaUtils.string(sortDirection).flatMap(Sort.Direction::fromOptionalString).orElse(Sort.Direction.ASC),
                JavaUtils.string(sortProperties).orElse(defaultSort)
        );
    }

    public static void checkArgumentCustom(boolean argument, Enum<?> errorMessage) {
        if (!argument) {
            throw buildHttpBadRequestException(errorMessage);
        }
    }

    public static HttpClientErrorException buildHttpBadRequestException(Enum<?> errorMessage) {
        return new HttpClientErrorException(BAD_REQUEST, getOrNull(errorMessage, Enum::name));
    }
}
