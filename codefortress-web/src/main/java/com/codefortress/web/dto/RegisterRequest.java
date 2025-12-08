package com.codefortress.web.dto;

import java.util.Set;

public record RegisterRequest(String username, String password, Set<String> roles) {}