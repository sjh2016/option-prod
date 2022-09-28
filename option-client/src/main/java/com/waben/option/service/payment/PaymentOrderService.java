package com.waben.option.service.payment;

import com.waben.option.common.interfaces.thirdparty.PaymentAPI;
import com.waben.option.common.interfaces.thirdparty.PaymentOrderAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.payment.PaymentOrderDTO;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.PaymentAdminPageRequest;
import com.waben.option.common.model.request.payment.PaymentUserPageRequest;
import com.waben.option.common.model.vo.payment.PaymentOrderVO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentOrderService {

    @Resource
    private PaymentAPI paymentAPI;

    @Resource
    private PaymentOrderAPI paymentOrderAPI;

    @Resource
    private ModelMapper modelMapper;

    public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request) {
        return paymentAPI.pay(userId, userIp, request);
    }

    public PageInfo<PaymentOrderDTO> userPage(Long userId, PaymentUserPageRequest req) {
        return paymentOrderAPI.userPage(userId, req);
    }

    public PageInfo<PaymentOrderDTO> adminPage(PaymentAdminPageRequest req) {
        return paymentOrderAPI.adminPage(req);
    }

    public PaymentOrderVO detail(Long id) {
        PaymentOrderDTO order = paymentOrderAPI.query(id);
        if (order != null) {
            return modelMapper.map(order, PaymentOrderVO.class);
        } else {
            return null;
        }
    }

    public PaymentOrderVO last(Long userId) {
        PaymentOrderDTO order = paymentOrderAPI.last(userId);
        if (order != null) {
            return modelMapper.map(order, PaymentOrderVO.class);
        } else {
            return null;
        }
    }

}
