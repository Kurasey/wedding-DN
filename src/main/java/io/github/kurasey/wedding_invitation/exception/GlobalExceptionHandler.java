package io.github.kurasey.wedding_invitation.exception;

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

    @ExceptionHandler(NotFoundFamily.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundFamily(NotFoundFamily ex, Model model, HttpServletRequest request) {
        logger.warn("Family not found: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorStatus", HttpStatus.NOT_FOUND.value());
        checkAndSetAdminFlag(request, model);
        return "error/custom-error";
    }

    @ExceptionHandler(NotFoundGuestException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundGuest(NotFoundGuestException ex, Model model, HttpServletRequest request) {
        logger.warn("Guest not found: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorStatus", HttpStatus.NOT_FOUND.value());
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
}