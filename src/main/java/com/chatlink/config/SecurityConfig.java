package com.chatlink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


//TODO: Improve Security Config for CSRF, CORS, etc.

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                // Public endpoints
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/verify/**",
                                "/forgot-password",
                                "/reset-password/**"
                        ).permitAll()
                        .anyRequest().access((authentication, context) -> {
                            Object loggedUser = context.getRequest()
                                    .getSession()
                                    .getAttribute("loggedUser");
                            return new AuthorizationDecision(loggedUser != null);
                        })
                )

                // When NOT authenticated, redirect to /login
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint((req, res, e) -> res.sendRedirect("/login"))
                )

                // Disable default Spring login
                .formLogin(form -> form.disable())

                // Session management with invalid session URL
                .sessionManagement(session ->
                        session
                                .invalidSessionUrl("/login")
                )

                // Logout configuration
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}
