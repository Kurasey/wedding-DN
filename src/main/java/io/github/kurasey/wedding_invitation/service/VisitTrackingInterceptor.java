package io.github.kurasey.wedding_invitation.service;

import io.github.kurasey.wedding_invitation.model.VisitHistoryRecord;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class VisitTrackingInterceptor implements HandlerInterceptor {

    VisitHistoryService historyService;

    public VisitTrackingInterceptor(VisitHistoryService historyService) {
        this.historyService = historyService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String URI = request.getRequestURI();
        String personalLink = extractPersonalLink(URI);

        if (personalLink != null) {
            VisitHistoryRecord record = new VisitHistoryRecord(
                    personalLink,
                    URI,
                    request.getHeader("User-Agent"),
                    request.getRemoteAddr()
            );
            historyService.save(record);
        }
        return true;
    }

    private String extractPersonalLink(String URI) {
        String[] parts = URI.split("/");
        if (parts.length >= 2) {
            return parts[1];
        }
        return null;
    }
}
