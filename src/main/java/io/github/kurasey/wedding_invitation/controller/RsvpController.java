package io.github.kurasey.wedding_invitation.controller;

import io.github.kurasey.wedding_invitation.controller.dto.GuestDto;
import io.github.kurasey.wedding_invitation.controller.dto.RsvpRequestDto;
import io.github.kurasey.wedding_invitation.service.RsvpService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/{personalLink}/rsvp")
public class RsvpController {

    private final RsvpService rsvpService;
    private static final Logger logger = LoggerFactory.getLogger(RsvpController.class);


    public RsvpController(RsvpService rsvpService) {
        this.rsvpService = rsvpService;
    }

    @PostMapping
    public ResponseEntity<Void> handleRsvp(@PathVariable String personalLink,
                                           @Valid @RequestBody RsvpRequestDto rsvpRequest) {
        rsvpService.processRsvp(personalLink, rsvpRequest);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Validation Error"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericRestException(Exception ex) {
        logger.error("An error occurred during RSVP processing", ex);
        Map<String, String> errorDetails = Map.of(
                "error", "Internal Server Error",
                "message", "Произошла внутренняя ошибка на сервере. Пожалуйста, попробуйте позже."
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/details")
    public ResponseEntity<List<GuestDto>> getRsvpDetails(@PathVariable String personalLink) {
        List<GuestDto> guests = rsvpService.getGuestDetailsForFamily(personalLink);
        return ResponseEntity.ok(guests);
    }
}