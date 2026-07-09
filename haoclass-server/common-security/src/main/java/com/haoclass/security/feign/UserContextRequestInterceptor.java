package com.haoclass.security.feign;

import com.haoclass.common.context.UserContextHeaders;
import com.haoclass.common.trace.TraceConstants;
import com.haoclass.security.config.CommonSecurityProperties;
import com.haoclass.security.context.AuthenticatedUser;
import com.haoclass.security.context.UserContextHolder;
import com.haoclass.security.trace.TraceContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.AllArgsConstructor;

/**
 * 在feign远程调用的时候自动调用这个拦截器 把用户信息塞进请求头里
 */
@AllArgsConstructor
public class UserContextRequestInterceptor implements RequestInterceptor {

    private final CommonSecurityProperties securityProperties;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(UserContextHeaders.INTERNAL_SECRET, securityProperties.getInternalSecret());
        requestTemplate.header(TraceConstants.TRACE_ID_HEADER, TraceContextHolder.getOrCreateTraceId());

        AuthenticatedUser user = UserContextHolder.getUser();
        if (user == null) {
            return;
        }

        requestTemplate.header(UserContextHeaders.USER_ID, user.getUserId().toString());
        requestTemplate.header(UserContextHeaders.USER_ROLE, user.getRole().toString());
    }
}
