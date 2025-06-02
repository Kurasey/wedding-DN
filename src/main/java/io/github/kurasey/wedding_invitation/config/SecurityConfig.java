package io.github.kurasey.wedding_invitation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

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
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception{
        return httpSecurity.authorizeHttpRequests(
                registry -> registry
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).authenticated()
                        .requestMatchers("/**").permitAll())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

}
