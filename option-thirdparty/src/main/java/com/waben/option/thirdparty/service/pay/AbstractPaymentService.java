package com.waben.option.thirdparty.service.pay;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.PaymentOrderAPI;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO;
import com.waben.option.common.model.dto.payment.PaymentOrderDTO;
import com.waben.option.common.model.dto.payment.PaymentPassagewayDTO;
import com.waben.option.common.model.dto.user.UserDTO;
import com.waben.option.common.model.enums.PaymentOrderStatusEnum;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.thirdparty.service.PaymentService;

/**
 * @author: Peter
 * @date: 2021/7/7 15:42
 */
public abstract class AbstractPaymentService implements PaymentService {

    @Resource
    private IdWorker idWorker;

    @Resource
    private PaymentOrderAPI paymentOrderAPI;
    
	@Resource
	private StaticConfig staticConfig;

    protected PaymentOrderDTO buildFormOrder(Long userId, PayFrontRequest request, UserDTO user, PaymentApiConfigDTO payApiConfig, PaymentApiConfigDTO.PaymentMethodDTO method, PaymentPassagewayDTO passageway) {
        Long orderId = idWorker.nextId();
        PaymentOrderDTO order = new PaymentOrderDTO();
        order.setId(orderId);
        order.setUserId(userId);
        order.setBrokerSymbol(user.getSymbol());
        order.setCashType(payApiConfig.getCashType());
        order.setOrderNo(String.valueOf(orderId));
        order.setStatus(PaymentOrderStatusEnum.PENDING);
        order.setReqNum(request.getReqNum());
        order.setReqMoney(request.getReqMoney());
        order.setRealMoney(BigDecimal.ZERO);
        order.setRealNum(BigDecimal.ZERO);
        order.setReqCurrency(request.getReqCurrency());
        order.setTargetCurrency(staticConfig.getDefaultCurrency());
        order.setFee(BigDecimal.ZERO);
        order.setPayApiId(payApiConfig.getId());
        order.setPayApiName(payApiConfig.getName());
        order.setPayMethodId(method.getId());
        order.setPayMethodName(method.getName());
        order.setPassagewayId(passageway.getId());
        order.setExchangeRate(request.getExchangeRate());
        order.setGmtCreate(LocalDateTime.now());
        order.setThirdOrderNo("UpNoResponse" + orderId);
        return order;
    }

    protected Long buildPaymentOrder(Long userId, PayFrontRequest request, UserDTO user, PaymentApiConfigDTO payApiConfig, PaymentApiConfigDTO.PaymentMethodDTO method, PaymentPassagewayDTO passageway) {
        verifyUser(user);
        // 创建订单
        Long orderId = idWorker.nextId();
        PaymentOrderDTO order = new PaymentOrderDTO();
        order.setId(orderId);
        order.setUserId(userId);
        order.setBrokerSymbol(user.getSymbol());
        order.setCashType(payApiConfig.getCashType());
        order.setOrderNo(String.valueOf(orderId));
        order.setStatus(PaymentOrderStatusEnum.PENDING);
        order.setReqNum(request.getReqNum());
        order.setReqMoney(request.getReqMoney());
        order.setRealMoney(BigDecimal.ZERO);
        order.setRealNum(BigDecimal.ZERO);
        order.setReqCurrency(request.getReqCurrency());
        order.setTargetCurrency(staticConfig.getDefaultCurrency());
        order.setFee(BigDecimal.ZERO);
        order.setPayApiId(payApiConfig.getId());
        order.setPayApiName(payApiConfig.getName());
        order.setPayMethodId(method.getId());
        order.setPayMethodName(method.getName());
        order.setPassagewayId(passageway.getId());
        order.setExchangeRate(request.getExchangeRate());
        order.setGmtCreate(LocalDateTime.now());
        paymentOrderAPI.createOrder(order);
        return orderId;
    }

    protected void verifyUser(UserDTO user) {
        // 判断用户手机号和邮箱地址是否存在
        if (StringUtils.isBlank(user.getName()) || StringUtils.isBlank(user.getEmail())) {
            throw new ServerException(2000);
        }
    }

    protected String mapStringToQuery(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!org.springframework.util.StringUtils.isEmpty(entry.getValue())) {
                builder.append(entry.getKey());
                builder.append("=");
                builder.append(entry.getValue());
                builder.append("&");
            }
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public String mapToQueryString(Map<String, Object> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!org.springframework.util.StringUtils.isEmpty(entry.getValue())) {
                builder.append(entry.getKey());
                builder.append("=");
                builder.append(entry.getValue());
                builder.append("&");
            }
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    protected String mapToString(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!"sign".equals(entry.getKey()) &&
                    !org.springframework.util.StringUtils.isEmpty(entry.getValue())) {
                builder.append(entry.getKey());
                builder.append("=");
                builder.append(entry.getValue());
                builder.append("&");
            }
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

}
