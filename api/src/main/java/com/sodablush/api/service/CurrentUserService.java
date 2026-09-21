package com.sodablush.api.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.sodablush.api.dto.UpdateProfileRequestDTO;
import com.sodablush.api.exception.BadRequestException;
import com.sodablush.api.exception.ForbiddenException;
import com.sodablush.api.model.User;
import com.sodablush.api.repository.UserRepository;

/**
 * Resuelve el usuario de la base de datos a partir del JWT de Auth0.
 * Si es la primera vez que el usuario llama a la API, se crea su fila
 * en USERS automaticamente (sync de identidad).
 */
@Service
public class CurrentUserService {

    /** Clave dentro de notification_settings donde guardamos el estado de la cuenta. */
    public static final String ACCOUNT_STATUS_KEY = "account_status";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_SUSPENDED = "suspended";

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Busca (o crea) el usuario ligado al token. */
    public User getOrCreateUser(Jwt jwt) {
        String auth0Id = jwt.getSubject();
        return userRepository.findByAuth0Id(auth0Id)
                .orElseGet(() -> createUser(jwt, auth0Id));
    }

    /** Igual que getOrCreateUser pero rechaza cuentas suspendidas. */
    public User requireActiveUser(Jwt jwt) {
        User user = getOrCreateUser(jwt);
        if (isSuspended(user)) {
            throw new ForbiddenException("La cuenta esta suspendida");
        }
        return user;
    }

    public boolean isSuspended(User user) {
        Map<String, Object> settings = user.getNotificationSettings();
        return settings != null && STATUS_SUSPENDED.equals(settings.get(ACCOUNT_STATUS_KEY));
    }

    public User updateProfile(User user, UpdateProfileRequestDTO request) {
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getLocale() != null) {
            user.setLocale(request.getLocale());
        }
        if (request.getTheme() != null) {
            user.setTheme(request.getTheme());
        }
        if (request.getSoundSettings() != null) {
            user.setSoundSettings(request.getSoundSettings());
        }
        if (request.getNotificationSettings() != null) {
            // Conservamos el estado de la cuenta aunque el cliente mande settings nuevos
            Map<String, Object> nuevos = new HashMap<>(request.getNotificationSettings());
            Map<String, Object> actuales = user.getNotificationSettings();
            if (actuales != null && actuales.containsKey(ACCOUNT_STATUS_KEY)) {
                nuevos.putIfAbsent(ACCOUNT_STATUS_KEY, actuales.get(ACCOUNT_STATUS_KEY));
            }
            user.setNotificationSettings(nuevos);
        }
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateAccountStatus(User user, String status) {
        if (!STATUS_ACTIVE.equals(status) && !STATUS_SUSPENDED.equals(status)) {
            throw new BadRequestException("Estado invalido: usa 'active' o 'suspended'");
        }
        Map<String, Object> settings = user.getNotificationSettings();
        Map<String, Object> nuevos = settings != null ? new HashMap<>(settings) : new HashMap<>();
        nuevos.put(ACCOUNT_STATUS_KEY, status);
        user.setNotificationSettings(nuevos);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private User createUser(Jwt jwt, String auth0Id) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setAuth0Id(auth0Id);
        user.setEmail(resolveEmail(jwt, auth0Id));
        user.setDisplayName(resolveDisplayName(jwt));
        // Semilla estable para el avatar Blobatar del cliente
        user.setAvatarSeed(UUID.nameUUIDFromBytes(auth0Id.getBytes(StandardCharsets.UTF_8)).toString());
        user.setLocale("es");
        user.setTheme("light");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Los tokens de la pestana Test de Auth0 (client-credentials) no traen email.
     * En ese caso usamos un placeholder unico basado en el sub.
     */
    private String resolveEmail(Jwt jwt, String auth0Id) {
        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) {
            return email;
        }
        String safe = auth0Id.replace('|', '-').replace('@', '-');
        return safe + "@users.sodablush.local";
    }

    private String resolveDisplayName(Jwt jwt) {
        String name = jwt.getClaimAsString("name");
        if (name == null || name.isBlank()) {
            name = jwt.getClaimAsString("nickname");
        }
        if (name == null || name.isBlank()) {
            name = jwt.getClaimAsString("email");
        }
        if (name == null || name.isBlank()) {
            name = "Soda Learner";
        }
        return name;
    }
}
