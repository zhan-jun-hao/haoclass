package com.haoclass.main.infrastructure.security;

import com.haoclass.main.infrastructure.persistence.po.User;
import com.haoclass.security.context.AuthenticatedUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
public class LoginUser implements UserDetails, AuthenticatedUser {

    private final Long id;

    private final String phone;

    private final String nickname;

    private final String password;

    private final Integer role;

    private final Integer status;

    private final List<GrantedAuthority> authorities;

    public LoginUser(User user) {
        this.id = user.getId();
        this.phone = user.getPhone();
        this.nickname = user.getNickname();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.authorities = List.of(new SimpleGrantedAuthority(Objects.equals(role, 1) ? "ROLE_ADMIN" : "ROLE_USER"));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return phone;
    }

    @Override
    public Long getUserId() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Objects.equals(status, 1);
    }
}
