package com.haoclass.ai.interfaces.controller.client;

import com.haoclass.ai.application.service.client.ClientAiChatApplicationService;
import com.haoclass.ai.interfaces.vo.ai.client.request.ClientAiChatReqVo;
import com.haoclass.ai.interfaces.vo.ai.client.response.ClientAiChatRespVo;
import com.haoclass.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户端AI客服接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/client/chat")
public class ClientAiChatController {

    private final ClientAiChatApplicationService clientAiChatApplicationService;

    /**
     * 向AI客服提问
     *
     * @param reqVo AI客服提问请求对象
     * @return AI客服回答
     */
    @PostMapping
    public Result<ClientAiChatRespVo> chat(@Valid @RequestBody ClientAiChatReqVo reqVo) {
        return Result.success(clientAiChatApplicationService.chat(reqVo));
    }
}
