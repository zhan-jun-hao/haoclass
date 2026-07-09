package com.haoclass.security.filter;

import com.haoclass.common.context.UserContextHeaders;
import com.haoclass.security.config.CommonSecurityProperties;
import com.haoclass.security.context.HeaderAuthenticatedUser;
import com.haoclass.security.context.SecurityRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private final CommonSecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null && isTrustedRequest(request)) {
            authenticate(request);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 校验request的内部密钥 是否来自gateway
     * @param request
     * @return
     */
    private boolean isTrustedRequest(HttpServletRequest request) {
        String internalSecret = request.getHeader(UserContextHeaders.INTERNAL_SECRET);
        return StringUtils.hasText(internalSecret)
                && Objects.equals(internalSecret, securityProperties.getInternalSecret());
    }

    private void authenticate(HttpServletRequest request) {
        String userIdHeader = request.getHeader(UserContextHeaders.USER_ID);
        String roleHeader = request.getHeader(UserContextHeaders.USER_ROLE);
        if (!StringUtils.hasText(userIdHeader) || !StringUtils.hasText(roleHeader)) {
            return;
        }

        try {
            Long userId = Long.valueOf(userIdHeader);
            Integer role = Integer.valueOf(roleHeader);
            SecurityRole securityRole = SecurityRole.of(role);
            if (securityRole == null) {
                return;
            }
            HeaderAuthenticatedUser principal = new HeaderAuthenticatedUser(userId, role);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(new SimpleGrantedAuthority(securityRole.getAuthority()))
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (NumberFormatException ignored) {
            SecurityContextHolder.clearContext();
        }
    }
}
