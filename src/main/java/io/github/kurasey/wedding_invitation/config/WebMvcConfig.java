package io.github.kurasey.wedding_invitation.config;

import io.github.kurasey.wedding_invitation.service.VisitTrackingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final VisitTrackingInterceptor visitTrackingInterceptor;

    public WebMvcConfig(VisitTrackingInterceptor visitTrackingInterceptor) {
        this.visitTrackingInterceptor = visitTrackingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(visitTrackingInterceptor).addPathPatterns("/{personalLink}");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("landing.html");
    }

    @Bean
    public RestTemplate template() {
        return new RestTemplate();
    }

}