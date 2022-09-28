package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_bank_code", autoResultMap = true)
public class BankCode extends BaseEntity<Long> {

    private String name;

    private String code;
    
    private String currency;
	/** 支持的通道ID（英文逗号分割） */
	private String supportUpId;
	/** 支付的通道银行代码（英文逗号分割） */
	private String supportUpCode;

    public static final String NAME = "name";
    public static final String CODE = "code";
    public static final String CURRENCY = "currency";
    public static final String PAY_API_ID = "pay_api_id";
}
