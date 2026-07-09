package com.haoclass.coupon.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoclass.coupon.domain.model.query.CouponTemplateQuery;
import com.haoclass.coupon.infrastructure.persistence.po.CouponTemplate;

import java.util.List;

public interface CouponTemplateService extends IService<CouponTemplate> {

    /**
     * 分页查询优惠券模板
     *
     * @param query
     * @return
     */
    IPage<CouponTemplate> pageList(CouponTemplateQuery query);

    /**
     * 查询优惠券详情
     *
     * @param id
     * @return
     */
    CouponTemplate getCouponTemplateById(Long id);

    /**
     * 新增优惠券
     *
     * @param couponTemplate
     */
    void saveCouponTemplate(CouponTemplate couponTemplate);

    /**
     * 修改优惠券模板
     *
     * @param id
     * @param couponTemplate
     */
    void updateCouponTemplateById(Long id, CouponTemplate couponTemplate);

    /**
     * 删除优惠券
     *
     * @param id
     */
    void deleteCouponTemplateById(Long id);

    /**
     * 发布优惠券
     *
     * @param id
     */
    void publishCouponTemplate(Long id);

    /**
     * 停用优惠券
     *
     * @param id
     */
    void stopCouponTemplate(Long id);

    /**
     * 查询所有已发放的可领取的优惠券
     *
     * @return
     */
    List<CouponTemplate> findPublishedList();

    /**
     * 查询一个已发放的可领取的优惠券
     * @param id
     * @return
     */
    CouponTemplate findPublishedOne(Long id);

    /**
     * 原子增加已领取数量
     * @param id
     */
    void increaseReceivedCount(Long id);
}
