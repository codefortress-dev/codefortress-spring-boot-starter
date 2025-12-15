package dev.codefortress.core.security;

import dev.codefortress.core.model.CodeFortressUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Adapter class that converts a {@link CodeFortressUser} into a {@link UserDetails} object
 * that Spring Security can understand.
 */
public class CodeFortressUserDetails implements UserDetails {

    private final CodeFortressUser user;

    public CodeFortressUserDetails(CodeFortressUser user) {
        this.user = user;
    }

    /**
     * Returns the authorities granted to the user.
     * It ensures that all roles start with the "ROLE_" prefix.
     *
     * @return a collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.roles().stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
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