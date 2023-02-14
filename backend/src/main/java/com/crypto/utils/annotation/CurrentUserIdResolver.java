package com.crypto.utils.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.crypto.users.security.services.Security.currentUserId;

public class CurrentUserIdResolver implements HandlerMethodArgumentResolver {

//    public CurrentUserIdResolver() {
//        System.out.println("Ready");
//    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUserId.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        String id = webRequest.getParameter("id");
//        System.out.println("parameter = " + id);
        return id == null ? currentUserId().toString() : id;
    }
}
