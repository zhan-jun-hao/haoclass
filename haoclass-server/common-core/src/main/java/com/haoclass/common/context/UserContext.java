package com.haoclass.common.context;

/**
 * gateway传递的用户上下文
 *
 * @param userId
 * @param role
 */
public record UserContext(Long userId, Integer role, Long authVersion) {
}
