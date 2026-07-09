package com.haoclass.security.context;


public interface AuthenticatedUser {

    Long getUserId();

    Integer getRole();
}
