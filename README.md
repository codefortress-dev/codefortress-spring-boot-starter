## 🏰 CodeFortress Security Starter

Seguridad profesional para Spring Boot, lista para usar. Autenticación JWT, Refresh Tokens Rotativos, Auditoría y Protección contra Fuerza Bruta en una sola dependencia.

## 📖 Tabla de Contenidos

1. Instalación Rápida

2. Configuración (YAML)

3. Guía de Extensión (SPIs)

4. Usar tu propia Base de Datos de Usuarios

5. Personalizar la Auditoría (Logs)

6. Cambiar el almacenamiento de Tokens

7. Sistema de Eventos

8. Solución de Problemas Comunes

🚀 Instalación Rápida

### 🛡️ Características Destacadas

#### 📱 Gestión Inteligente de Sesiones
CodeFortress implementa un sistema de **"Ventana Deslizante"** para los Refresh Tokens.
* Puedes configurar `max-sessions: 1` para máxima seguridad (estilo Banca).
* O `max-sessions: 5` para permitir múltiples dispositivos (estilo Streaming).
* El sistema limpia automáticamente las sesiones más antiguas cuando se alcanza el límite.

#### 🧱 Rate Limiting (Anti-Brute Force)
Protección nativa **In-Memory** basada en el algoritmo *Token Bucket*.
* Bloquea IPs que intentan adivinar contraseñas o saturar el endpoint de login.
* Configurable por número de intentos y ventana de tiempo.
* *Nota: En la versión Community, el límite es por instancia de servidor.*

#### 🔐 Política de Contraseñas Híbrida
No impongas reglas arbitrarias. CodeFortress valida la longitud mínima por defecto, pero permite inyectar tu propia **Expresión Regular (Regex)** desde la configuración para cumplir con normativas específicas (NIST, PCI-DSS) sin recompilar código.



## Agrega la dependencia en tu pom.xml.


```xml
<dependency>
    <groupId>dev.codefortress</groupId>
    <artifactId>codefortress-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
Al iniciar tu aplicación, CodeFortress creará automáticamente las tablas necesarias (cf_users, cf_roles, cf_refresh_tokens) si usas una base de datos SQL.

## ⚙️ Configuración Maestra

Copia y pega esto en tu application.yml. Todo es opcional, pero estos son los valores recomendados.

```YML
codefortress:
  # 1. API REST: Habilita los endpoints /auth/login y /auth/register
  api:
    enabled: true
    auth-path: "/auth"

  # 2. Seguridad: Tiempos de vida de los tokens
  security:
    jwt-secret: "TU_CLAVE_BASE64_DEBE_SER_MUY_LARGA_PARA_SER_SEGURA_MIN_256BITS=="
    jwt-expiration-ms: 900000        # 15 minutos (Access Token)
    refresh-token:
      enabled: true                  # Habilita el sistema de Refresh Tokens
      expiration-ms: 2592000000      # 30 días

    # Definición de Roles por Ruta
    routes:
      - pattern: "/api/public/**"
        roles: ["PUBLIC"]            # Acceso libre
      - pattern: "/api/admin/**"
        roles: ["ADMIN"]             # Requiere Rol ADMIN

  # 3. Contraseñas: Define qué tan complejas deben ser
  password:
    min-length: 8
    # Descomenta para usar Regex avanzada (ej: 1 Mayus, 1 Num, 1 Símbolo)
    # regexp: "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$"
    # regexp-error-message: "La contraseña es muy débil."

  # 4. Rate Limiting: Protege contra ataques de fuerza bruta
  rate-limit:
    enabled: true
    max-attempts: 5         # 5 intentos fallidos permitidos
    duration-seconds: 60    # Se recargan en 1 minuto

  # 5. CORS: Permite que tu Frontend (React/Angular) se conecte
  cors:
    enabled: true
    allowed-origins:
      - "http://localhost:3000"
      - "https://mi-dominio.com"
```
## ⚙️ Configuración Avanzada

CodeFortress viene listo para usar, pero puedes ajustar cada tornillo en tu `application.yml`:

```yaml
codefortress:
  # 1. API y Rutas
  api:
    auth-path: "/auth"      # Prefijo base (ej: /auth/login)

  # 2. Seguridad y Sesiones
  security:
    jwt-secret: "TU_CLAVE_SECRETA_DEBE_SER_LARGA_Y_COMPLEJA_PARA_PROD"
    jwt-expiration-ms: 900000        # 15 minutos (Access Token)

    refresh-token:
      enabled: true
      expiration-ms: 2592000000      # 30 días
      # Control de Sesiones Concurrentes (Nuevo en v1.0)
      # 1  = Estricto (Banco). Al loguearse en otro lado, cierra la sesión anterior.
      # 3  = Flexible (Netflix). Permite 3 dispositivos. El 4º dispositivo borra el 1º.
      # -1 = Ilimitado.
      max-sessions: 1

  # 3. Política de Contraseñas (Hardening)
  password:
    min-length: 8
    # Opcional: Regex para exigir Mayúsculas, Números y Especiales
    # regexp: "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$"
    # regexp-error-message: "La contraseña debe tener Mayúscula, Número y Símbolo."

  # 4. Protección Fuerza Bruta (Rate Limiting)
  rate-limit:
    enabled: true
    max-attempts: 5         # Bloquea tras 5 fallos seguidos
    duration-seconds: 60    # El bloqueo dura 1 minuto (recarga gradual)




## 🔌🔌 Guía de Extensión (Personaliza Todo)

CodeFortress usa Arquitectura Hexagonal. Esto significa que puedes reemplazar cualquier pieza de la lógica implementando una Interfaz (SPI). Si defines tu propio Bean, CodeFortress desactiva el suyo automáticamente.

1. Usar tu propia Base de Datos de Usuarios
   Si tienes un sistema Legacy, o usas MongoDB, o una tabla de usuarios preexistente, implementa CodeFortressUserProvider.

Caso de Uso: Tu empresa ya tiene una tabla EMPLEADOS y quieres usarla para el login.
```JAVA
@Service // Importante: Debe ser un Bean de Spring
public class LegacyUserProvider implements CodeFortressUserProvider {

    @Autowired
    private EmpleadoRepository empleadoRepo; // Tu repositorio existente

    @Override
    public Optional<CodeFortressUser> findByUsername(String username) {
        // 1. Buscas en tu sistema
        return empleadoRepo.findByEmail(username)
                .map(emp -> new CodeFortressUser(
                        emp.getEmail(),
                        emp.getPasswordHash(), // Debe estar en BCrypt
                        Set.of("USER"),        // Asigna roles
                        emp.isActivo()
                ));
    }

    @Override
    public CodeFortressUser save(CodeFortressUser user) {
        // Opcional: Implementar si quieres usar el endpoint /register
        throw new UnsupportedOperationException("El registro no está permitido en sistema Legacy");
    }
}
```
2. Personalizar la Auditoría (Logs)
   Por defecto, CodeFortress imprime los eventos de seguridad en la consola (Log). Si quieres guardarlos en una base de datos o enviarlos a Slack/Datadog, implementa CodeFortressAuditProvider.

Caso de Uso: Guardar intentos de login fallidos en una tabla SQL audit_logs.
```JAVA
@Service
public class DatabaseAuditProvider implements CodeFortressAuditProvider {

    @Autowired
    private AuditRepository auditRepository; // Tu repositorio

    @Override
    public void log(AuditRecord record) {
        // CodeFortress te entrega: Quién, Qué hizo, Cuándo y Detalles
        MyAuditEntity entity = new MyAuditEntity();
        entity.setUsername(record.principal());
        entity.setAction(record.action()); // Ej: LOGIN_FAILURE, REGISTER_USER
        entity.setTimestamp(record.timestamp());

        auditRepository.save(entity);
    }
}
```
3. Cambiar el almacenamiento de Tokens

Por defecto, los Refresh Tokens se guardan en la tabla cf_refresh_tokens usando JPA. Si prefieres usar Redis (por velocidad) o Mongo, implementa CodeFortressRefreshTokenProvider.
```JAVA
@Service
public class RedisTokenProvider implements CodeFortressRefreshTokenProvider {

    @Autowired
    private RedisTemplate<String, String> redis;

    @Override
    public CodeFortressRefreshToken create(String username, long expirationMs) {
        // Lógica para guardar en Redis...
        return new CodeFortressRefreshToken(uuid, username, expiry);
    }
    
    // ... implementar findByToken, deleteByToken ...
}
```
## 🔔 Sistema de Eventos

CodeFortress publica eventos de Spring para que puedas reaccionar a lo que sucede sin acoplar tu código.

Ejemplo: Enviar un Email de Bienvenida al registrarse

```JAVA
@Component
public class EmailListener {

    // Escucha el evento propio de CodeFortress
    @EventListener
    public void handleRegistro(CodeFortressUserCreatedEvent event) {
        String email = event.user().username();
        System.out.println("📧 Enviando bienvenida a: " + email);
    }
    
    // También puedes escuchar eventos nativos de Spring Security
    @EventListener
    public void handleLoginExitoso(AuthenticationSuccessEvent event) {
        System.out.println("✅ Usuario logueado: " + event.getAuthentication().getName());
    }
}
```
## ❓ Solución de Problemas Comunes

| Error / Código | Síntoma / Mensaje | Causa Probable | Solución |
| :--- | :--- | :--- | :--- |
| **401 Unauthorized** | `Bad credentials` o `Token expired` | Usuario/Pass incorrectos o el JWT ha vencido. | Verifica credenciales. Si es token, usa el `/refresh-token` o aumenta `jwt-expiration-ms`. |
| **403 Forbidden** | Acceso denegado (sin body) | El usuario está logueado pero **no tiene el rol** requerido para esa ruta. | Revisa la sección `routes` en `application.yml` y los roles asignados al usuario. |
| **429 Too Many Requests** | `Has excedido los intentos...` | El Rate Limiter bloqueó la IP por seguridad tras varios fallos. | Espera el tiempo indicado o ajusta `codefortress.rate-limit` en el YAML. |
| **400 Bad Request** | `La contraseña es muy débil...` | La contraseña no cumple la política de seguridad configurada. | Cumple los requisitos o ajusta `codefortress.password` (minLength o regexp). |
| **CORS Error** | (En consola del navegador) `Blocked by CORS policy` | El Frontend corre en un dominio/puerto distinto al Backend. | Activa `codefortress.cors.enabled: true` y agrega la URL en `allowed-origins`. |
| **Respuesta sin Refresh Token** | El campo `refreshToken` llega `null` en el login. | La funcionalidad de Refresh Token está desactivada. | Asegúrate de tener `codefortress.security.refresh-token.enabled: true`. |
| **500 Internal Server Error** | `UsernameNotFoundException` en `/register` o `/login` | Estás enviando un Token viejo en el Header `Authorization` a una ruta pública. | **Limpia los Headers** en Postman/Cliente. No envíes token para loguearte o registrarte. |

## 📝 Endpoints Disponibles

Por defecto, la ruta base es `/auth`, pero es configurable vía `codefortress.api.auth-path`.

| Método | Endpoint | Descripción | Auth Requerida | Body Esperado (JSON) |
| :--- | :--- | :--- | :---: | :--- |
| `POST` | `/auth/login` | Autentica credenciales. Retorna `accessToken` (JWT) y `refreshToken`. | ❌ No | `{"username": "admin", "password": "123"}` |
| `POST` | `/auth/register` | Registra un nuevo usuario y dispara el evento de creación. | ❌ No | `{"username": "user", "password": "123", "roles": ["USER"]}` |
| `POST` | `/auth/refresh-token` | Rota el Refresh Token y entrega un nuevo Access Token. | ❌ No | `{"refreshToken": "550e8400-e29b-..."}` |




## 🛠 Arquitectura

* CodeFortress sigue una Arquitectura Hexagonal (Puertos y Adaptadores) estricta:

* Core: Lógica pura, DTOs y Servicios de Dominio. Sin dependencias de Frameworks de BD.

* JPA Adapter: Implementación opcional que usa Hibernate/Spring Data.

* Web: Controladores REST.

* Starter: Módulo de Auto-Configuración condicional.


## Made with ❤️ by CodeFortress Team.