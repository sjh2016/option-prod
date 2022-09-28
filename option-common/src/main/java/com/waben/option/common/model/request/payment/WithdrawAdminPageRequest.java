package com.waben.option.common.model.request.payment;

import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.enums.WithdrawOrderStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WithdrawAdminPageRequest {

    /**
     * 出入金类型
     */
    private PaymentCashType cashType;
    /**
     * 支付单号
     */
    private String orderNo;
    /**
     * 第三方单号
     */
    private String thirdOrderNo;
    /**
     * 通道apiID
     */
    private Long payApiId;
    /**
     * 状态，多个用逗号分隔
     */
    private WithdrawOrderStatusEnum status;
    /**
     * 用户ID列表
     */
    private List<Long> uidList;
    /**
     * 代理标识列表
     */
    private List<String> brokerSymbolList;
    /**
     * 下单开始时间
     */
    private LocalDateTime startTime;
    /**
     * 下单结束时间
     */
    private LocalDateTime endTime;
    /**
     * 到账开始时间
     */
    private LocalDateTime arrivalStart;
    /**
     * 到账结束时间
     */
    private LocalDateTime arrivalEnd;
    /**
     * 电话号码
     */
    private String mobilePhone;
    /**
     * 页码
     */
    private int page;
    /**
     * 每页大小
     */
    private int size;
    
    private int limit;
    private Boolean isBlack;

    private String topId;

}
