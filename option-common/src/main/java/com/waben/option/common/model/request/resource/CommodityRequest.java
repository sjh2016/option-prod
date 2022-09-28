package com.waben.option.common.model.request.resource;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/6/23 19:12
 */
@Data
public class CommodityRequest {

    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 数量
     */
    private BigDecimal returnRate;

    /**
     * 每日收益
     */
    private BigDecimal income;

    /**
     * 规格（功率）
     */
    private String specification;

    /**
     * 价格
     */
    private BigDecimal actualPrice;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 数量
     */
    private Integer volume;

    /**
     * 是否启用
     */
    private Boolean enable;
}
