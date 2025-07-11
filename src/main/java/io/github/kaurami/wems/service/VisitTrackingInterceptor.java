package io.github.kaurami.wems.service;

import io.github.kaurami.wems.model.Family;
import io.github.kaurami.wems.model.VisitHistoryRecord;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
public class VisitTrackingInterceptor implements HandlerInterceptor {

    private final VisitHistoryService historyService;
    private final FamilyService familyService;

    public VisitTrackingInterceptor(VisitHistoryService historyService, FamilyService familyService) {
        this.historyService = historyService;
        this.familyService = familyService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables != null && pathVariables.containsKey("personalLink")) {
            String personalLink = pathVariables.get("personalLink");

            if (personalLink != null && !personalLink.contains("admin")) {
                try {
                    Family family = familyService.getByPersonalLink(personalLink);
                    VisitHistoryRecord record = new VisitHistoryRecord(
                            family,
                            request.getRequestURI(),
                            request.getHeader("User-Agent"),
                            request.getRemoteAddr()
                    );
                    historyService.save(record);
                } catch (Exception e) {
                    // Игнорируем ошибку, если семья не найдена, чтобы не ломать просмотр
                }
            }
        }
        return true;
    }
}