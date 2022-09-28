package com.waben.option.service.resource;

import com.waben.option.common.interfaces.resource.PaymentWayAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.PaymentWayDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PaymentWayService {

    @Resource
    private PaymentWayAPI paymentWayAPI;

    public PageInfo<PaymentWayDTO> queryList() {
        return paymentWayAPI.queryList();
    }

}
