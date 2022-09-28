package com.waben.option.service.resource;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waben.option.common.interfacesadmin.resource.AdminPaymentWayAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.PaymentWayDTO;

@Service
public class PaymentWayService {

    @Resource
    private AdminPaymentWayAPI adminPaymentWayAPI;

    public PageInfo<PaymentWayDTO> queryList() {
        return adminPaymentWayAPI.queryList();
    }

}
