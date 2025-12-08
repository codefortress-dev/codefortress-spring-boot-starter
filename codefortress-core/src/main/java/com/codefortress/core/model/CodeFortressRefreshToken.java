package com.codefortress.core.model;

import java.time.Instant;

public record CodeFortressRefreshToken(
        String token,
        String username,
        Instant expiryDate
) {}