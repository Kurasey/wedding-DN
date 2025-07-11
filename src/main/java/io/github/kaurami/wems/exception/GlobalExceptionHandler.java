package io.github.kaurami.wems.exception;

import jakarta.servlet.http.HttpServletRequest; // <-- ИМПОРТ
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ClientAbortException.class)
    public ResponseEntity<Void> handleClientAbortException(ClientAbortException ex, HttpServletRequest request) {
        final String remoteAddr = request.getRemoteAddr();
        logger.warn("ClientAbortException: connection aborted by client [{}], URL [{}]", remoteAddr, request.getRequestURL());
        // Ничего не возвращаем клиенту, так как он уже отключился.
        // Просто возвращаем статус, чтобы Spring завершил обработку.
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private void checkAndSetAdminFlag(HttpServletRequest request, Model model) {
        if (request.getRequestURI().startsWith("/admin")) {
            model.addAttribute("isAdminError", true);
        }
    }


    @ExceptionHandler({NotFoundFamily.class, NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception ex, Model model, HttpServletRequest request) {
        String message;
        if (ex instanceof NotFoundFamily) {
            logger.warn("Not found by personal link: {}", ex.getMessage());
            message = "Приглашение по этой ссылке не найдено. Пожалуйста, проверьте правильность адреса.";
        } else {
            logger.warn("Resource or handler not found for [{}]: {}", request.getMethod(), request.getRequestURI());
            message = "Страница, которую вы ищете, не существует.";
        }

        model.addAttribute("errorStatus", HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorMessage", message);
        checkAndSetAdminFlag(request, model);
        return "error/custom-error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model, HttpServletRequest request) {
        logger.error("An unexpected error occurred: ", ex);
        model.addAttribute("errorMessage", "Произошла непредвиденная ошибка на сервере.");
        model.addAttribute("errorStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        checkAndSetAdminFlag(request, model);
        return "error/custom-error";
    }

    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public String handleRateLimitExceeded(RateLimitExceededException ex, Model model, HttpServletRequest request) {
        logger.warn("Rate limit page triggered for IP {}: {}", request.getRemoteAddr(), ex.getMessage());
        model.addAttribute("errorStatus", HttpStatus.TOO_MANY_REQUESTS.value());
        model.addAttribute("errorMessage", ex.getMessage());
        checkAndSetAdminFlag(request, model);
        return "error/custom-error";
    }
}