package com.waben.option.common.model.dto.order;

import com.waben.option.common.model.enums.PurchaseGoodEnum;
import com.waben.option.common.model.enums.PurchaseGoodStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PurchaseGoodsDTO {

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 名
     */
    private String name;

    /**
     * 姓
     */
    private String surname;

    /**
     * 电话
     */
    private String phone;

    /**
     * 商品类型
     */
    private PurchaseGoodEnum type;

    /**
     * 街道
     */
    private String streetName;

    /**
     * 市区
     */
    private String urbanArea;

    /**
     * 省份
     */
    private String province;

    /**
     * 邮编
     */
    private String postCode;

    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 总金额
     */
    private BigDecimal totalPrice;

    /**
     * 状态
     */
    private PurchaseGoodStatusEnum status;

    /**
     * 到期时间
     */
    private LocalDate expireDate;

    /**
     * 图片信息
     */
    private String picture0;

    /**
     * 图片信息
     */
    private String picture1;

    /**
     * 图片信息
     */
    private String picture2;

    /**
     * 图片信息
     */
    private String picture3;

    private LocalDateTime gmtCreate;

}
