# Blush-API

API de Soda Blush: aprendizaje gamificado de CSS (latas = conceptos, tragos = pasos).
Spring Boot (Java 17) + JPA + PostgreSQL (Neon) + Auth0 como proveedor de identidad.

## Como correr la API

1. Copia `.env.example` como `.env` en esta carpeta (`api/`) y llena los valores.
2. Ejecuta:

```powershell
.\mvnw.cmd spring-boot:run
```

La API queda en `http://localhost:8080`. Prueba con `GET /api/health` (unico endpoint publico).

> IMPORTANTE: la password de Neon estuvo expuesta en git. Hay que **rotarla** en el
> dashboard de Neon (Roles -> Reset password) y actualizar el `.env`.

## Setup de Auth0 (desde cero, una sola vez)

1. Crea una cuenta/tenant en [auth0.com](https://auth0.com) (ej. `sodablush`).
2. **Applications -> APIs -> Create API**:
   - Name: `Blush API`
   - Identifier (Audience): `https://api.sodablush.com` (no necesita ser una URL real)
   - Signing algorithm: RS256
3. **Applications -> Create Application**:
   - Tipo: *Single Page Application* (para la app React)
   - En Settings agrega los callback/logout/web origins de tu frontend
     (ej. `http://localhost:5173`).
4. Copia al `.env`:
   - `AUTH0_ISSUER_URI` = `https://TU-TENANT.us.auth0.com/` (con slash final)
   - `AUTH0_AUDIENCE` = el Identifier de la API (paso 2)

El cliente hace login con Auth0 (Universal Login + PKCE, scopes `openid profile email`)
y manda el access token en cada request: `Authorization: Bearer <token>`.
La API valida el token (issuer + audience) y crea/sincroniza el usuario en la tabla
`USERS` usando el claim `sub` -> columna `auth0_id`. El registro, login y
recuperacion de contraseña los maneja Auth0; la API no guarda contraseñas.

### Probar con un token real

En Auth0: API -> Test -> copia el `access_token` y usalo:

```powershell
curl http://localhost:8080/api/cans -H "Authorization: Bearer TU_TOKEN"
```

## Endpoints

Todos requieren `Authorization: Bearer <token>` excepto `/api/health`.

### Curriculum (lectura)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/health` | Estado del servicio (publico) |
| GET | `/api/cans` | Latas con su estado de bloqueo para el usuario |
| GET | `/api/cans/{id}` | Detalle de una lata (galeria) |
| GET | `/api/cans/{canId}/drinks` | Tragos de la lata en orden |
| GET | `/api/drinks/{id}` | Detalle del trago (definicion/sintaxis + ids de test/ejercicio) |
| GET | `/api/drinks/{drinkId}/exercises` | Prueba final del trago (sin la respuesta) |
| GET | `/api/drinks/{drinkId}/test` | Mini-test del trago (sin respuestas correctas) |

### Progreso (consumir latas)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/api/cans/{canId}/start` | Abre la lata (si esta desbloqueada) |
| POST | `/api/drinks/{drinkId}/complete` | Completa un trago de teoria/sintaxis y avanza |
| GET | `/api/me/progress` | Resumen de progreso (latas y tragos) |
| POST | `/api/me/progress/reset` | Reinicia el progreso (los logros se conservan) |

### Mini-test (segundo trago)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/api/tests/{testId}/attempts` | Inicia un intento (con vidas) |
| POST | `/api/tests/attempts/{attemptId}/answers` | Responde una pregunta (pierde vida si falla) |
| POST | `/api/tests/attempts/{attemptId}/finish` | Cierra el intento; si aprueba completa el trago |

### Prueba final (cuarto trago)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/api/exercises/{exerciseId}/submit` | Valida el CSS; si es correcto completa el trago |

`CODE_EXERCISES.validation_rules` (JSON) soporta:

```json
{ "requiredContains": ["display: flex"], "forbidden": ["float"] }
```

Sin reglas, se compara contra `expected_css` normalizando espacios y mayusculas.

### Perfil y cuenta
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/me` | Perfil del usuario (se crea al primer request) |
| PATCH | `/api/me` | Actualiza displayName, locale, theme y settings |
| PATCH | `/api/me/status` | `{"status": "suspended"}` o `"active"` |
| GET | `/api/me/achievements` | Logros del usuario |
| GET | `/api/achievements` | Catalogo de logros |
| POST | `/api/feedback` | Enviar comentario/bug (`type`, `message`) |

Nota: el estado de la cuenta se guarda como flag `account_status` dentro del JSON
`notification_settings` de `USERS` (no existe columna dedicada en el schema actual).

## Logros (seed requerido)

Los logros del blueprint se otorgan automaticamente al completar todas las latas,
pero deben existir en la tabla `ACHIEVEMENTS` con estos `code`:

```sql
INSERT INTO achievements (id, code, name, description, type, points) VALUES
  (gen_random_uuid(), 'SENOR_DE_LAS_LATAS', 'Señor de las latas',
   'Terminaste todas las latas de la estanteria', 'completion', 100),
  (gen_random_uuid(), 'SENOR_DEL_RECICLAJE', 'Señor del reciclaje',
   'Reiniciaste tu progreso y volviste a terminar todas las latas', 'completion', 200);
```

## Reglas de negocio implementadas

- Desbloqueo secuencial: una lata se desbloquea cuando `unlock_order <= max completado + 1`.
- El mini-test usa vidas (`lives_allowed`, default 3): fallar resta vida; sin vidas = "sin gas" (reprobado, puede reintentar segun `max_attempts`).
- `passing_score` acepta porcentaje (70) o fraccion (0.7); default 70.
- Aprobar test o ejercicio marca el trago como completado; el ultimo trago completa la lata.
- Reset de progreso conserva los logros (habilita "Señor del reciclaje").
- Cuenta suspendida: solo puede usar `/api/me` (para reactivarse).

## Tests

```powershell
.\mvnw.cmd test
```

Cubre grading de ejercicios, sync Auth0, unlock/complete de latas y que `/api/health` es publico.
El contexto completo (`@SpringBootTest`) no se levanta en CI porque necesita Neon + un issuer Auth0 real.

Seed de logros (una vez en Neon): `src/main/resources/db/achievements-seed.sql`.
