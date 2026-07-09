package com.haoclass.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haoclass.security.filter.HeaderAuthenticationFilter;
import com.haoclass.security.filter.TraceIdFilter;
import com.haoclass.security.handler.CommonAccessDeniedHandler;
import com.haoclass.security.handler.CommonAuthenticationEntryPoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@AutoConfiguration
@AutoConfigureBefore(UserDetailsServiceAutoConfiguration.class)
@EnableMethodSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(CommonSecurityProperties.class)
public class CommonSecurityAutoConfiguration {

    /**
     * 公共的未登录处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public CommonAuthenticationEntryPoint commonAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new CommonAuthenticationEntryPoint(objectMapper);
    }

    /**
     * 已登录但没权限处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public CommonAccessDeniedHandler commonAccessDeniedHandler(ObjectMapper objectMapper) {
        return new CommonAccessDeniedHandler(objectMapper);
    }

    /**
     * 验证gateway请求
     * @param securityProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public HeaderAuthenticationFilter headerAuthenticationFilter(CommonSecurityProperties securityProperties) {
        return new HeaderAuthenticationFilter(securityProperties);
    }

    /**
     * HTTP入口链路追踪过滤器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public TraceIdFilter traceIdFilter() {
        return new TraceIdFilter();
    }

    /**
     * 让traceId尽量早进入MDC
     *
     * @param filter
     * @return
     */
    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration(TraceIdFilter filter) {
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    /**
     * 不要让HeaderAuthenticationFilter自动注册到Servlet链 我们只在feign时调用
     * @param filter
     * @return
     */
    @Bean
    public FilterRegistrationBean<HeaderAuthenticationFilter> disableHeaderAuthenticationFilterRegistration(
            HeaderAuthenticationFilter filter) {
        FilterRegistrationBean<HeaderAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * 登录只在gateway就行了 下游服务不用走这个了
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService headerOnlyUserDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("Password login is not supported by this service");
        };
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain commonSecurityFilterChain(
            HttpSecurity http,
            HeaderAuthenticationFilter headerAuthenticationFilter,
            CommonAuthenticationEntryPoint authenticationEntryPoint,
            CommonAccessDeniedHandler accessDeniedHandler) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/health", "/error").permitAll()
                        .requestMatchers("/api/*/inner/**").permitAll()
                        .requestMatchers("/api/pay/notify/**").permitAll()
                        .requestMatchers("/api/*/admin/**", "/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(headerAuthenticationFilter, AnonymousAuthenticationFilter.class)
                .build();
    }
}
