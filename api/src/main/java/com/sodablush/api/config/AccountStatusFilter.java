package com.sodablush.api.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sodablush.api.model.User;
import com.sodablush.api.service.CurrentUserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Cuenta suspendida: solo puede usar /api/me y /api/me/status (para reactivarse).
 * El resto de /api/** responde 403.
 */
public class AccountStatusFilter extends OncePerRequestFilter {

    private final CurrentUserService currentUserService;

    public AccountStatusFilter(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/health")
                || path.startsWith("/api/health/")
                || path.equals("/api/me")
                || path.equals("/api/me/status");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            User user = currentUserService.getOrCreateUser(jwt);
            if (currentUserService.isSuspended(user)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                String body = "{\"timestamp\":\"" + LocalDateTime.now()
                        + "\",\"status\":403,\"error\":\"Forbidden\","
                        + "\"message\":\"La cuenta esta suspendida\"}";
                response.getWriter().write(body);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
