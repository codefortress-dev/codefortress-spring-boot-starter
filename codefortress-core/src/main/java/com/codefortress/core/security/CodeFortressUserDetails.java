package com.codefortress.core.security;

import com.codefortress.core.model.CodeFortressUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Clase adaptadora: Convierte nuestro usuario agnóstico en algo que Spring Security entiende.
 */
public class CodeFortressUserDetails implements UserDetails {

    private final CodeFortressUser user;

    public CodeFortressUserDetails(CodeFortressUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Spring convention
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.password();
    }

    @Override
    public String getUsername() {
        return user.username();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return user.enabled(); }
}