package io.github.kurasey.wedding_invitation.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        if (exception instanceof BadCredentialsException) {
            String ip = request.getRemoteAddr();
            Bucket bucket = buckets.computeIfAbsent(ip, this::createNewBucket);

            if (!bucket.tryConsume(1)) {
                response.sendRedirect(request.getContextPath() + "/login/locked");
                return; // Важно завершить выполнение
            }
        }

        response.sendRedirect(request.getContextPath() + "/login?error=true");
    }

    private Bucket createNewBucket(String key) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)))) // 5 попыток в минуту
                .build();
    }
}