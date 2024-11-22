package com.magenta.configuration;

import com.magenta.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.authorizeHttpRequests(authRequest -> {
            // Rutas públicas
            authRequest.requestMatchers(
                    "/login",
                    "/register",
                    "/",
                    "/home"
            ).permitAll();

            // Recursos estáticos
            authRequest.requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/uploads/**"
            ).permitAll();

            // Rutas protegidas de citas que requieren autenticación
            authRequest.requestMatchers(
                    "/appointments/**",
                    "/api/appointments/**",
                    "/questionnaire/**",
                    "/notifications/**"
            ).authenticated();

            // Rutas de usuario que requieren autenticación
            authRequest.requestMatchers(
                    "/modifyUser",
                    "/user/info"
            ).authenticated();

            authRequest.requestMatchers(
                    "/admin/**"
            ).hasRole("ADMIN");
        });


        http.sessionManagement(sessionManager-> sessionManager
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://localhost:9090")); // Actualiza el puerto frontend
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
