package com.demo.app.exception;

import com.demo.app.models.ApiError;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBadRequest_withIllegalArgumentException_returns400() {
        // given
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test/path");

        // when
        ResponseEntity<ApiError> response = handler.handleBadRequest(ex, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(body.getError()).isEqualTo("Bad Request");
        assertThat(body.getMessage()).isEqualTo("Invalid argument");
        assertThat(body.getPath()).isEqualTo("/test/path");
        assertThat(body.getTimestamp()).isNotNull();
    }

    @Test
    void handleBadRequest_withMethodArgumentTypeMismatch_returns400() {
        // given
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException(
                        "123", Integer.class, "id", null, new IllegalArgumentException("type mismatch"));
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/metrics");

        // when
        ResponseEntity<ApiError> response = handler.handleBadRequest(ex, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(body.getError()).isEqualTo("Bad Request");
        assertThat(body.getPath()).isEqualTo("/metrics");
        assertThat(body.getTimestamp()).isNotNull();
    }

    @Test
    void handleBadRequest_withMissingServletRequestParameter_returns400() throws Exception {
        // given
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("window", "String");
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/metrics/top-pages");

        // when
        ResponseEntity<ApiError> response = handler.handleBadRequest(ex, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(body.getError()).isEqualTo("Bad Request");
        assertThat(body.getPath()).isEqualTo("/metrics/top-pages");
        assertThat(body.getTimestamp()).isNotNull();
    }

    @Test
    void handleInternal_withGenericException_returns500() {
        // given
        Exception ex = new RuntimeException("Something went wrong");
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/internal/error");

        // when
        ResponseEntity<ApiError> response = handler.handleInternal(ex, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getError()).isEqualTo("Internal Server Error");
        assertThat(body.getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(body.getPath()).isEqualTo("/internal/error");
        assertThat(body.getTimestamp()).isNotNull();
    }
}
