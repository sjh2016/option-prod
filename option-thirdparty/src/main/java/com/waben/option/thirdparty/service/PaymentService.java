package com.waben.option.thirdparty.service;

import com.waben.option.common.model.dto.payment.*;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO.PaymentMethodDTO;
import com.waben.option.common.model.dto.user.UserDTO;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
import org.springframework.util.StringUtils;

import java.util.Map;

public interface PaymentService {

	/**
	 * 请求支付
	 *
	 * <pre>
	 * 	1、type为1，表示跳转支付页面，content为支付页面地址
	 * {
	 * 		"type": 1,
	 * 		"content": "http://test.com/pay"
	 * }
	 *
	 * 2、type为2，表示返回支付卡片信息，content为卡片信息
	 * {
	 * 		"type": 2,
	 * 		"content": {
	 * 			"name": "张三",
	 * 			"card": "622021505211545852",
	 * 			"bank": "中国银行",
	 * 			"amount": 100.05,
	 * 			"remark": "852364"
	 * 		}
	 * }
	 *
	 * 3、type为3，表示返回充币钱包地址，content为钱包地址
	 * {
	 * 		"type": 3,
	 * 		"content": "0xcb2bbd022f0e0a5749fb6fbc9c60f47986658b94"
	 * }
	 *
	 * 4、type为4，表示新窗口跳转支付页面，content为支付页面地址
	 * {
	 * 		"type": 4,
	 * 		"content": "http://test.com/pay"
	 * }
	 * </pre>
	 */
	public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request, UserDTO user,
			PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method, PaymentPassagewayDTO passageway);

	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data);

	public WithdrawSystemResult withdraw(WithdrawSystemRequest request, UserDTO user, WithdrawOrderDTO order,
			PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method);

	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data);

	public default String mapToQueryString(Map<String, Object> map) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			builder.append(entry.getKey());
			builder.append("=");
			builder.append(entry.getValue());
			builder.append("&");
		}
		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}

	public default String mapToValueString(Map<String, Object> map) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			builder.append(entry.getValue());
		}
		return builder.toString();
	}

}
