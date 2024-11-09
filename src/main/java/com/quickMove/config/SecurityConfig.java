package com.quickMove.config;


import com.quickMove.service.UserServices;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserServices userService;

    private final JWTFilterService jwtFilterService;

    public SecurityConfig(UserServices userService, JWTFilterService jwtFilterService) {
        this.userService = userService;
        this.jwtFilterService = jwtFilterService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers("/api/users/**","history","/api/users/login").permitAll()
                                                         .requestMatchers("api/rides/history","api/rides/cancel").hasAnyAuthority(
                                                                 "ROLE_passenger","ROLE_driver")
                                                         .requestMatchers("api/rides/check","api/rides/accept",
                                                                 "api/rides/complete").hasAuthority(
                                                                 "ROLE_driver")
                                                         .requestMatchers("/api/rides/book").hasAuthority(
                        "ROLE_passenger").
                        requestMatchers("/api/admin/**").hasAuthority("ROLE_admin").
                                                                 anyRequest().authenticated()).sessionManagement(manager ->manager.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS)).authenticationProvider(authenticationProvider()).addFilterBefore(
                                    jwtFilterService, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandlingConfigurer -> {
                    exceptionHandlingConfigurer.authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"message\":\"You are not authenticated to perform this action\"}");
                    });
                    exceptionHandlingConfigurer.accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"message\":\"You are not authorized to perform this action\"}");
                    });
                });

                            return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider =new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService.userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
    }

}