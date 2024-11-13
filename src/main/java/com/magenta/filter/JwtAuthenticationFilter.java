package com.magenta.filter;

import com.magenta.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/register",
            "/login",
            "/",
            "/home"
    );

    private static final List<String> PROTECTED_URLS = Arrays.asList(
            "/user/info",
            "/modifyUser"
    );

    private static final List<String> STATIC_RESOURCES = Arrays.asList(
            "/css/",
            "/js/",
            "/images/",
            "/uploads/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String path = request.getRequestURI();
            log.debug("Processing request for path: {}", path);

            // Permitir recursos estáticos
            if (isStaticResource(path)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Si es una URL pública, permitir acceso
            if (isPublicUrl(path)) {
                log.debug("Public URL accessed: {}", path);
                filterChain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader("Authorization");
            log.debug("Auth header present: {}", authHeader != null);

            // Si es una ruta protegida y no hay token, denegar acceso
            if (isProtectedUrl(path)) {
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    log.warn("Protected URL accessed without valid token: {}", path);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }

            // Procesar el token si existe
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                processToken(authHeader, request);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Error in JWT filter: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void processToken(String authHeader, HttpServletRequest request) {
        try {
            String jwt = authHeader.substring(7);
            String username = jwtService.getUsernameFromToken(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("User authenticated: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Error processing token: ", e);
        }
    }

    private boolean isPublicUrl(String path) {
        return PUBLIC_URLS.stream().anyMatch(path::equals);
    }

    private boolean isProtectedUrl(String path) {
        return PROTECTED_URLS.stream().anyMatch(path::equals);
    }

    private boolean isStaticResource(String path) {
        return STATIC_RESOURCES.stream().anyMatch(path::startsWith);
    }
}