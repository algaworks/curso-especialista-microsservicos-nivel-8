package com.algaworks.algashop.ecommerce.application.exception;

import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import com.algaworks.algashop.ecommerce.infraestructure.oauth2.OAuth2UserAuthorizationRequiredException;
import com.algaworks.algashop.ecommerce.infraestructure.security.AlgaShopSecurityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AlgaShopSecurityService algaShopSecurityService;
    private final EcommerceProperties ecommerceProperties;

    @ExceptionHandler(OAuth2UserAuthorizationRequiredException.class)
    public String handleOAuth2UserAuthorizationRequired(
            OAuth2UserAuthorizationRequiredException e,
            HttpServletRequest request) {
        log.warn(e.getMessage(), e);
        algaShopSecurityService.forceLogout();
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:" + ecommerceProperties.getAuthWithAlgaSecurityPath();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String genericError(Exception e) {
        log.error(e.getMessage(), e);
        return "error/500";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFound() {
        return "error/404";
    }

}
