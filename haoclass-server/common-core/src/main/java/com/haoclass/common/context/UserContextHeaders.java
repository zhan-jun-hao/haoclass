package com.haoclass.common.context;

/**
 * 请求头常量类
 * Header名称
 */
public final class UserContextHeaders {

    public static final String USER_ID = "X-User-Id";

    public static final String USER_ROLE = "X-User-Role";

    /**
     * 这个校验请求是不是来自gateway
     */
    public static final String INTERNAL_SECRET = "X-Internal-Secret";

    /**
     * 私有化构造方法 不让别人重复new出来
     */
    private UserContextHeaders() {
    }
}
