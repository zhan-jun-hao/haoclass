package com.haoclass.main.interfaces.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.admin.AdminDeadMessagesApplicationService;
import com.haoclass.main.interfaces.vo.mq.request.AdminMqDeadMessagePageReqVo;
import com.haoclass.main.interfaces.vo.mq.response.AdminMqDeadMessageBasicRespVo;
import com.haoclass.main.interfaces.vo.mq.response.AdminMqDeadMessageDetailRespVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理-admin端-死信消息管理
 *
 * @author zhanjunhao
 * @since 2026/06/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main/admin/mq-dead-messages")
public class AdminMqDeadMessagesController {

    private final AdminDeadMessagesApplicationService adminDeadMessagesApplicationService;

    /**
     * 分页查询死信消息
     *
     * @param reqVo
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult<AdminMqDeadMessageBasicRespVo>> pageList(@Validated AdminMqDeadMessagePageReqVo reqVo) {
        log.info("分页查询死信信息: {}", JSONObject.toJSONString(reqVo));
        return Result.success(adminDeadMessagesApplicationService.getDeadMessagesPageList(reqVo));
    }

    /**
     * 查询死信消息详情
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<AdminMqDeadMessageDetailRespVo> getDetailVo(@PathVariable("id") Long id) {
        log.info("详细查询死信信息: {}", id);
        return Result.success(adminDeadMessagesApplicationService.getDetail(id));
    }

    /**
     * 重新投递死信消息
     *
     * @param id
     * @return
     */
    @PostMapping("{id}/retry")
    public Result<Void> retryDeadMessage(@PathVariable("id") Long id) {
        log.info("重投死信信息: {}", id);
        adminDeadMessagesApplicationService.retryDeadMessage(id);
        return Result.success();
    }

    /**
     * 忽略死信消息
     *
     * @param id
     * @return
     */
    @PostMapping("{id}/ignore")
    public Result<Void> ignoreDeadMessage(@PathVariable("id") Long id) {
        log.info("忽略死信信息: {}", id);
        adminDeadMessagesApplicationService.ignoreDeadMessage(id);
        return Result.success();
    }

}
