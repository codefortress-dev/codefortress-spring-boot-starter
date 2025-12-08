package com.example.demo;

import com.codefortress.web.dto.LoginRequest;
import com.codefortress.web.dto.RefreshTokenRequest;
import com.codefortress.web.dto.RegisterRequest;
import com.codefortress.web.dto.TokenResponse;
import com.codefortress.core.model.CodeFortressUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RefreshTokenIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // CONFIGURACIÓN EXTRA: Evitar el error "streaming mode" en caso de fallo
    @TestConfiguration
    static class Config {
        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            // Usamos el cliente moderno de Java.
            // Maneja los flujos de entrada/salida y los reintentos de auth (401) perfectamente.
            return new RestTemplateBuilder()
                    .requestFactory(JdkClientHttpRequestFactory.class);
        }
    }

    @Test
    @DisplayName("Flujo Completo: Registro -> Login -> Refresh -> Rotación -> Bloqueo")
    void testFullRefreshTokenFlow() {
        // 1. DATOS UNIFICADOS (Usa estos para todo el test)
        String testUsername = "testUser_" + System.currentTimeMillis();
        // Password fuerte para pasar cualquier política configurada
        String testPassword = "StrongPassword123@";

        // =================================================================================
        // PASO 1: REGISTRO
        // =================================================================================
        RegisterRequest registerReq = new RegisterRequest(testUsername, testPassword, Set.of("USER"));

        // CAMBIO: Recibimos String.class para poder leer el error si ocurre
        ResponseEntity<String> rawResponse = restTemplate.postForEntity("/auth/register", registerReq, String.class);

        // Debug en consola para que veas qué pasó
        System.out.println("Status Registro: " + rawResponse.getStatusCode());
        System.out.println("Body Registro: " + rawResponse.getBody());

        assertThat(rawResponse.getStatusCode())
                .withFailMessage("El registro falló con status %s y cuerpo: %s", rawResponse.getStatusCode(), rawResponse.getBody())
                .isEqualTo(HttpStatus.OK);

        // =================================================================================
        // PASO 2: LOGIN
        // =================================================================================
        // Usamos LAS MISMAS variables. No hardcodeamos "admin" ni "123".
        LoginRequest loginReq = new LoginRequest(testUsername, testPassword);
        ResponseEntity<TokenResponse> loginResp = restTemplate.postForEntity("/auth/login", loginReq, TokenResponse.class);

        assertThat(loginResp.getStatusCode())
                .withFailMessage("El login falló con estado: %s", loginResp.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(loginResp.getBody()).isNotNull();

        String accessToken1 = loginResp.getBody().accessToken();
        String refreshToken1 = loginResp.getBody().refreshToken();

        System.out.println("✅ Login Exitoso. Refresh Token 1: " + refreshToken1);

        // =================================================================================
        // PASO 3: REFRESH
        // =================================================================================
        RefreshTokenRequest refreshReq1 = new RefreshTokenRequest(refreshToken1);
        ResponseEntity<TokenResponse> refreshResp = restTemplate.postForEntity("/auth/refresh-token", refreshReq1, TokenResponse.class);

        assertThat(refreshResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        String refreshToken2 = refreshResp.getBody().refreshToken();
        System.out.println("✅ Refresh Exitoso. Refresh Token 2: " + refreshToken2);

        assertThat(refreshToken2).isNotEqualTo(refreshToken1);

        // =================================================================================
        // PASO 4: REUSE ATTACK (Debe fallar)
        // =================================================================================
        ResponseEntity<String> errorResp = restTemplate.postForEntity("/auth/refresh-token", refreshReq1, String.class);

        System.out.println("Intento de reuso bloqueado con estado: " + errorResp.getStatusCode());
        assertThat(errorResp.getStatusCode().is2xxSuccessful()).isFalse();
    }
}