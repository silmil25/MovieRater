package com.web.movierater.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.movierater.models.dtos.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse er = new ErrorResponse();
        er.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        er.setError("Unauthorized");
        er.setMessage(authException.getMessage());
        er.setTimestamp(OffsetDateTime.now());
        er.setPath(request.getServletPath());

        new ObjectMapper().writeValue(response.getOutputStream(), er);
    }
}
