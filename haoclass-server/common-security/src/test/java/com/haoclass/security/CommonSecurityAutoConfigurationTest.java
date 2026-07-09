package com.haoclass.security;

import com.haoclass.common.context.UserContextHeaders;
import com.haoclass.common.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = CommonSecurityAutoConfigurationTest.TestApplication.class,
        properties = {
                "haoclass.security.internal-secret=test-internal-secret",
                "spring.autoconfigure.exclude="
                        + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                        + "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration,"
                        + "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration"
        }
)
@AutoConfigureMockMvc
class CommonSecurityAutoConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRejectAnonymousRequest() throws Exception {
        performRequest()
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void shouldRejectForgedUserHeadersWithoutInternalSecret() throws Exception {
        mockMvc.perform(post("/api/test/admin/templates")
                        .header(UserContextHeaders.USER_ID, "1")
                        .header(UserContextHeaders.USER_ROLE, "1")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void shouldRejectNormalUserFromAdminApi() throws Exception {
        mockMvc.perform(post("/api/test/admin/templates")
                        .header(UserContextHeaders.INTERNAL_SECRET, "test-internal-secret")
                        .header(UserContextHeaders.USER_ID, "1")
                        .header(UserContextHeaders.USER_ROLE, "0")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void shouldAllowAdminUser() throws Exception {
        mockMvc.perform(post("/api/test/admin/templates")
                        .header(UserContextHeaders.INTERNAL_SECRET, "test-internal-secret")
                        .header(UserContextHeaders.USER_ID, "1")
                        .header(UserContextHeaders.USER_ROLE, "1")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private org.springframework.test.web.servlet.ResultActions performRequest() throws Exception {
        return mockMvc.perform(post("/api/test/admin/templates")
                .contentType("application/json")
                .content("{}"));
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(TestController.class)
    static class TestApplication {
    }

    @RestController
    @RequestMapping("/api/test/admin/templates")
    static class TestController {

        @PostMapping
        Result<Void> create() {
            return Result.success();
        }
    }
}
