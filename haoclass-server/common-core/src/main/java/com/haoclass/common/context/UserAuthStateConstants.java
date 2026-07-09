package com.haoclass.common.context;

public final class UserAuthStateConstants {

    public static final String KEY_PREFIX = "haoclass:auth:user:";

    public static final String FIELD_STATUS = "status";

    public static final String FIELD_ROLE = "role";

    public static final String FIELD_AUTH_VERSION = "authVersion";

    public static final int ENABLED_STATUS = 1;

    private UserAuthStateConstants() {
    }

    public static String key(Long userId) {
        return KEY_PREFIX + userId;
    }
}
