package io.github.kaurami.wems.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    private final CustomAuthenticationFailureHandler failureHandler;
    private final AssetLocations assetLocations;

    public SecurityConfig(CustomAuthenticationFailureHandler failureHandler, AssetLocations assetLocations) {
        this.failureHandler = failureHandler;
        this.assetLocations = assetLocations;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(@Value("${admin.username}") String adminUserName,
                                                 @Value("${admin.password}") String adminPassword) {
        return new InMemoryUserDetailsManager(User.builder()
                .username(adminUserName)
                .password(passwordEncoder().encode(adminPassword))
                .roles("ADMIN")
                .build());
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/{personalLink}/**")).permitAll()
                        .requestMatchers("/**").permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .defaultSuccessUrl("/admin", true)
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/perform_logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"))
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .contentSecurityPolicy(csp -> csp.policyDirectives(String.join(" ",
                                        "default-src 'self';",
                                        "script-src 'self' 'unsafe-inline' https://api-maps.yandex.ru https://yandex.st https://*.yandex.net https://yastatic.net https://code.jquery.com https://cdn.jsdelivr.net https://stackpath.bootstrapcdn.com https://kit.fontawesome.com;",
                                        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://stackpath.bootstrapcdn.com;",
                                        "font-src 'self' https://fonts.gstatic.com;",
                                        "img-src 'self' data: https://*.maps.yandex.net https://api-maps.yandex.ru https://yandex.ru " + assetLocations.getBaseUrl() + ";",
                                        "media-src 'self' " + assetLocations.getBaseUrl() + ";",
                                        "connect-src 'self' https://*.api-maps.yandex.ru https://*.yandex.net https://yastatic.net;",
                                        "frame-src 'self' https://*.yandex.net;"
                                )
                        ))
                );

        return http.build();
    }
}