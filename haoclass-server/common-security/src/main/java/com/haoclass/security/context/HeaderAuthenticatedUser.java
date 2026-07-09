package com.haoclass.security.context;

/**
 * 请求头字段
 * @param userId
 * @param role
 */
public record HeaderAuthenticatedUser(Long userId, Integer role) implements AuthenticatedUser {

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public Integer getRole() {
        return role;
    }
}
