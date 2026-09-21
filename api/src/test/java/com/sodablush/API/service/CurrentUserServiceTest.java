package com.sodablush.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import com.sodablush.api.exception.ForbiddenException;
import com.sodablush.api.model.User;
import com.sodablush.api.repository.UserRepository;

/** Sync de identidad Auth0 -> USERS (sin base de datos). */
class CurrentUserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CurrentUserService service = new CurrentUserService(userRepository);

    private Jwt jwtFor(String sub) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject(sub)
                .claim("email", "ana@sodablush.com")
                .claim("name", "Ana")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();
    }

    @Test
    void createsUserOnFirstRequest() {
        when(userRepository.findByAuth0Id("auth0|123")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User creado = service.getOrCreateUser(jwtFor("auth0|123"));

        assertEquals("auth0|123", creado.getAuth0Id());
        assertEquals("ana@sodablush.com", creado.getEmail());
        assertEquals("Ana", creado.getDisplayName());
        assertNotNull(creado.getId());
        assertNotNull(creado.getAvatarSeed());
    }

    @Test
    void avatarSeedIsStableForSameSubject() {
        when(userRepository.findByAuth0Id(any())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User primero = service.getOrCreateUser(jwtFor("auth0|abc"));
        User segundo = service.getOrCreateUser(jwtFor("auth0|abc"));

        assertEquals(primero.getAvatarSeed(), segundo.getAvatarSeed());
    }

    @Test
    void reusesExistingUser() {
        User existente = new User();
        existente.setAuth0Id("auth0|123");
        when(userRepository.findByAuth0Id("auth0|123")).thenReturn(Optional.of(existente));

        User resultado = service.getOrCreateUser(jwtFor("auth0|123"));

        assertEquals(existente, resultado);
    }

    @Test
    void createsUserWithoutEmailUsesPlaceholder() {
        when(userRepository.findByAuth0Id("auth0|no-mail")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("auth0|no-mail")
                .claim("name", "Test")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();

        User creado = service.getOrCreateUser(jwt);

        assertEquals("auth0-no-mail@users.sodablush.local", creado.getEmail());
    }

    @Test
    void suspendedAccountIsRejected() {
        User suspendido = new User();
        suspendido.setAuth0Id("auth0|123");
        suspendido.setNotificationSettings(Map.of(CurrentUserService.ACCOUNT_STATUS_KEY,
                CurrentUserService.STATUS_SUSPENDED));
        when(userRepository.findByAuth0Id("auth0|123")).thenReturn(Optional.of(suspendido));

        assertThrows(ForbiddenException.class, () -> service.requireActiveUser(jwtFor("auth0|123")));
        assertTrue(service.isSuspended(suspendido));
    }
}
