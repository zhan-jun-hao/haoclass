package com.haoclass.main.infrastructure.security;

import com.haoclass.main.domain.service.UserService;
import com.haoclass.main.infrastructure.persistence.po.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userService.findExistUserByPhone(phone);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
        return new LoginUser(user);
    }
}
