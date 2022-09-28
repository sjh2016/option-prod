package com.waben.option.data.entity.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.PurchaseGoodEnum;
import com.waben.option.common.model.enums.PurchaseGoodStatusEnum;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_u_purchase_goods")
public class PurchaseGoods extends BaseEntity<Long> {

    private Long userId;

    private String name;

    private String surname;

    private PurchaseGoodEnum type;

    private String streetName;

    private String urbanArea;

    private String province;

    private String postCode;

    private String phone;

    private BigDecimal price;
    
    private Integer quantity;
    
    private BigDecimal totalPrice;

    private PurchaseGoodStatusEnum status;

    private LocalDate expireDate;

    private String picture0;

    private String picture1;

    private String picture2;

    private String picture3;

    public static final String USER_ID = "user_id";
    public static final String TYPE = "type";
    public static final String STATUS = "status";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";

}
