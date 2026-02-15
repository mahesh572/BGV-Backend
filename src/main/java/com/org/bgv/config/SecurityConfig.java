package com.org.bgv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.org.bgv.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;
    
    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/configuration/ui",
                    "/configuration/security",
                    "/actuator/**"
                ).permitAll()
                .anyRequest().authenticated()
            )

            .sessionManagement(sess ->
                sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .userDetailsService(userDetailsService)

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)   // 401
                .accessDeniedHandler((request, response, ex2) -> {    // 403
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("""
                    {
                      "success": false,
                      "message": "Forbidden: Insufficient permissions",
                      "status": 403
                    }
                    """);
                })
            )

            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)

            .build();
    }

    @Bean
    public JwtAuthFilter jwtFilter() {
        return new JwtAuthFilter();
    }
/*
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and().build();
    }
    */
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            PortalAuthenticationProvider provider
    ) throws Exception {

        return http
            .getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(provider)
            .build();
    }
}