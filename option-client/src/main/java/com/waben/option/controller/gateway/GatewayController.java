package com.waben.option.controller.gateway;

import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.payment.BindCardDTO;
import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.common.model.enums.PlatformEnum;
import com.waben.option.common.model.enums.RegisterEnum;
import com.waben.option.common.model.query.UserAccountStatementQuery;
import com.waben.option.common.model.request.order.OrderRequest;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.PaymentUserPageRequest;
import com.waben.option.common.model.request.payment.WithdrawOtcFrontRequest;
import com.waben.option.common.model.request.payment.WithdrawUserPageRequest;
import com.waben.option.common.model.request.resource.ClientSunshineRequest;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.controller.account.AccountController;
import com.waben.option.controller.activity.ActivityController;
import com.waben.option.controller.code.CodeController;
import com.waben.option.controller.order.OrderController;
import com.waben.option.controller.payment.BindCardController;
import com.waben.option.controller.payment.PaymentOrderController;
import com.waben.option.controller.payment.PaymentPassagewayController;
import com.waben.option.controller.payment.WithdrawOrderController;
import com.waben.option.controller.resource.BankCodeController;
import com.waben.option.controller.resource.CommodityController;
import com.waben.option.controller.resource.ConfigController;
import com.waben.option.controller.resource.SunshineClientController;
import com.waben.option.controller.user.LoginController;
import com.waben.option.controller.user.UserController;
import com.waben.option.mode.gateway.activity.GatewayActivityJoinStatusListReq;
import com.waben.option.mode.gateway.activity.GatewayActivityReceiveReq;
import com.waben.option.mode.gateway.activity.GatewaySunshineLastPendingReq;
import com.waben.option.mode.gateway.activity.GatewaySunshineSubmitReq;
import com.waben.option.mode.gateway.order.GatewayDynamicProductOrderPageDataReq;
import com.waben.option.mode.gateway.order.GatewayPlaceOrderReq;
import com.waben.option.mode.gateway.order.GatewayProductOrderPageDataReq;
import com.waben.option.mode.gateway.payment.*;
import com.waben.option.mode.gateway.user.GatewayAccountFlowPageDataReq;
import com.waben.option.mode.gateway.user.GatewayLoginReq;
import com.waben.option.mode.gateway.user.GatewayRegisterReq;
import com.waben.option.mode.gateway.user.GatewaySendVerifyCodeReq;
import com.waben.option.mode.request.ClientLoginRequest;
import com.waben.option.mode.request.ClientRegisterUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/gateway")
public class GatewayController extends AbstractBaseController {

	@Resource
	private LoginController loginController;

	@Resource
	private CodeController codeController;

	@Resource
	private UserController userController;

	@Resource
	private AccountController accountController;

	@Resource
	private PaymentPassagewayController paymentPassagewayController;

	@Resource
	private PaymentOrderController paymentOrderController;

	@Resource
	private WithdrawOrderController withdrawOrderController;

	@Resource
	private BindCardController bindCardController;

	@Resource
	private BankCodeController bankCodeController;

	@Resource
	private CommodityController commodityController;

	@Resource
	private OrderController orderController;

	@Resource
	private ActivityController activityController;

	@Resource
	private ConfigController configController;

	@Resource
	private SunshineClientController sunshineClientController;

	@RequestMapping(value = "entry", method = RequestMethod.POST)
	public ResponseEntity<?> entry(@RequestHeader("action") String action, @RequestBody String json) {
		log.info("action:" + action + ", gateway entry req:" + json);
		switch (action) {
		case "login":
			return login(json);
		case "register":
			return register(json);
		case "sendVerifyCode":
			return sendVerifyCode(json);
		case "sendImageCode":
			return sendImageCode(json);
		case "userSta":
			return userSta(json);
		case "accountFlowPageData":
			return accountFlowPageData(json);
		case "queryPayPassagewayList":
			return queryPayPassagewayList(json);
		case "payPlaceOrder":
			return payPlaceOrder(json);
		case "payPageData":
			return payPageData(json);
		case "withdrawPlaceOrder":
			return withdrawPlaceOrder(json);
		case "withdrawPageData":
			return withdrawPageData(json);
		case "bankList":
			return bankList(json);
		case "bindCardList":
			return bindCardList(json);
		case "queryBindCard":
			return queryBindCard(json);
		case "saveBindCard":
			return saveBindCard(json);
		case "updateBindCard":
			return updateBindCard(json);
		case "deleteBindCard":
			return deleteBindCard(json);
		case "productList":
			return productList(json);
		case "productHotList":
			return productHotList(json);
		case "productPlaceOrder":
			return productPlaceOrder(json);
		case "productOrderPageData":
			return productOrderPageData(json);
		case "productDynamicOrderPageData":
			return productDynamicOrderPageData(json);
		case "activityJoinStatusList":
			return activityJoinStatusList(json);
		case "activityReceive":
			return activityReceive(json);
		case "queryImgBaseUrl":
			return queryImgBaseUrl(json);
		case "sunshineLastPending":
			return sunshineLastPending(json);
		case "sunshineSubmit":
			return sunshineSubmit(json);
		default:
			return ok();
		}
	}

	private ResponseEntity<?> login(String json) {
		GatewayLoginReq req = JacksonUtil.decode(json, GatewayLoginReq.class);
		ClientLoginRequest realReq = new ClientLoginRequest();
		realReq.setUsername(req.getUsername());
		realReq.setPassword(req.getPassword());
		realReq.setAuthorityType(AuthorityEnum.CLIENT);
		realReq.setPlatform(PlatformEnum.H5);
		return loginController.login(realReq);
	}

	@SuppressWarnings("unchecked")
	private ResponseEntity<?> register(String json) {
		GatewayRegisterReq req = JacksonUtil.decode(json, GatewayRegisterReq.class);
		ClientRegisterUserRequest realReq = new ClientRegisterUserRequest();
		realReq.setUsername(req.getUsername());
		realReq.setPassword(req.getPassword());
		realReq.setRegisterType(req.getRegisterType());
		if (req.getRegisterType() == RegisterEnum.PHONE) {
			// 验证图片验证码
			ResponseEntity<Response<Boolean>> verifyResult = (ResponseEntity<Response<Boolean>>) codeController
					.verifyImageCode(req.getSessionId(), req.getVerifyCode());
			if (!verifyResult.getBody().getData()) {
				throw new ServerException(1031);
			}
		}
		realReq.setAuthorityType(AuthorityEnum.CLIENT);
		realReq.setPlatform(PlatformEnum.H5);
		realReq.setVerifyCode(req.getVerifyCode());
		realReq.setAreaCode(req.getAreaCode());
		realReq.setSource(1);
		realReq.setSymbolCode(req.getSymbolCode());
		realReq.setSessionId(req.getSessionId());
		return userController.registerWithToken(realReq);
	}

	private ResponseEntity<?> sendImageCode(String json) {
		return codeController.imageCodeGenerate();
	}

	private ResponseEntity<?> sendVerifyCode(String json) {
		GatewaySendVerifyCodeReq req = JacksonUtil.decode(json, GatewaySendVerifyCodeReq.class);
		if (req.getType() == RegisterEnum.EMAIL) {
			req.setAreaCode(null);
		} else {
			if (StringUtils.isBlank(req.getAreaCode())) {
				req.setAreaCode("62");
			}
		}
		return codeController.send(req.getAreaCode(), req.getUsername());
	}

	private ResponseEntity<?> userSta(String json) {
		return userController.sta(0);
	}

	private ResponseEntity<?> accountFlowPageData(String json) {
		GatewayAccountFlowPageDataReq req = JacksonUtil.decode(json, GatewayAccountFlowPageDataReq.class);
		UserAccountStatementQuery realReq = new UserAccountStatementQuery();
		realReq.setPage(req.getPage());
		realReq.setSize(req.getSize());
		return accountController.queryStatementPage(realReq);
	}

	private ResponseEntity<?> queryPayPassagewayList(String json) {
		GatewayQueryPayPassagewayReq req = JacksonUtil.decode(json, GatewayQueryPayPassagewayReq.class);
		return paymentPassagewayController.queryDisplayList(req.getCashTypes());
	}

	private ResponseEntity<?> payPlaceOrder(String json) {
		GatewayPayPlaceOrderReq req = JacksonUtil.decode(json, GatewayPayPlaceOrderReq.class);
		PayFrontRequest realReq = new PayFrontRequest();
		realReq.setPassagewayId(req.getPassagewayId());
		realReq.setReqMoney(req.getReqMoney());
		realReq.setReqCurrency(req.getReqCurrency());
		realReq.setBankCode(req.getBankCode());
		return paymentOrderController.placeOrder(realReq);
	}

	private ResponseEntity<?> payPageData(String json) {
		GatewayPayPageDataReq req = JacksonUtil.decode(json, GatewayPayPageDataReq.class);
		PaymentUserPageRequest realReq = new PaymentUserPageRequest();
		realReq.setPage(req.getPage());
		realReq.setSize(req.getSize());
		return paymentOrderController.userPage(realReq);
	}

	private ResponseEntity<?> withdrawPlaceOrder(String json) {
		GatewayWithdrawPlaceOrderReq req = JacksonUtil.decode(json, GatewayWithdrawPlaceOrderReq.class);
		WithdrawOtcFrontRequest realReq = new WithdrawOtcFrontRequest();
		realReq.setReqNum(req.getReqNum());
		realReq.setTargetCurrency(req.getTargetCurrency());
		realReq.setPassagewayId(req.getPassagewayId());
		realReq.setBindId(req.getBindId());
		return withdrawOrderController.placeOtcOrder(realReq);
	}

	private ResponseEntity<?> withdrawPageData(String json) {
		GatewayWithdrawPageDataReq req = JacksonUtil.decode(json, GatewayWithdrawPageDataReq.class);
		WithdrawUserPageRequest realReq = new WithdrawUserPageRequest();
		realReq.setPage(req.getPage());
		realReq.setSize(req.getSize());
		return withdrawOrderController.userPage(realReq);
	}

	private ResponseEntity<?> bankList(String json) {
		return bankCodeController.query(null, null);
	}

	private ResponseEntity<?> bindCardList(String json) {
		return bindCardController.list();
	}

	private ResponseEntity<?> queryBindCard(String json) {
		GatewayQueryBindCardReq req = JacksonUtil.decode(json, GatewayQueryBindCardReq.class);
		return bindCardController.query(req.getId());
	}

	private ResponseEntity<?> saveBindCard(String json) {
		GatewaySaveBindCardReq req = JacksonUtil.decode(json, GatewaySaveBindCardReq.class);
		BindCardDTO realReq = new BindCardDTO();
		realReq.setName(req.getName());
		realReq.setBankCode(req.getBankCode());
		realReq.setBranchName(req.getBranchName());
		realReq.setBankCardId(req.getBankCardId());
		realReq.setMobilePhone(req.getMobilePhone());
		return bindCardController.bind(realReq);
	}

	private ResponseEntity<?> updateBindCard(String json) {
		GatewayUpdateBindCardReq req = JacksonUtil.decode(json, GatewayUpdateBindCardReq.class);
		BindCardDTO realReq = new BindCardDTO();
		realReq.setId(req.getId());
		realReq.setName(req.getName());
		realReq.setBankCode(req.getBankCode());
		realReq.setBranchName(req.getBranchName());
		realReq.setBankCardId(req.getBankCardId());
		realReq.setMobilePhone(req.getMobilePhone());
		return bindCardController.update(realReq);
	}

	private ResponseEntity<?> deleteBindCard(String json) {
		GatewayDeleteBindCardReq req = JacksonUtil.decode(json, GatewayDeleteBindCardReq.class);
		BindCardDTO realReq = new BindCardDTO();
		realReq.setId(req.getId());
		return bindCardController.delete(realReq);
	}

	private ResponseEntity<?> productList(String json) {
		return commodityController.queryPage(0, 20);
	}

	private ResponseEntity<?> productHotList(String json) {
		return commodityController.hot();
	}

	private ResponseEntity<?> productPlaceOrder(String json) {
		GatewayPlaceOrderReq req = JacksonUtil.decode(json, GatewayPlaceOrderReq.class);
		OrderRequest realReq = new OrderRequest();
		realReq.setCommodityId(req.getProductId());
		realReq.setVolume(BigDecimal.ONE);
		return orderController.place(realReq);
	}

	private ResponseEntity<?> productOrderPageData(String json) {
		GatewayProductOrderPageDataReq req = JacksonUtil.decode(json, GatewayProductOrderPageDataReq.class);
		return orderController.queryPage(req.getStatus(), req.getPage(), req.getSize());
	}

	private ResponseEntity<?> productDynamicOrderPageData(String json) {
		GatewayDynamicProductOrderPageDataReq req = JacksonUtil.decode(json,
				GatewayDynamicProductOrderPageDataReq.class);
		return orderController.dynamicPage(req.getPage(), req.getSize());
	}

	private ResponseEntity<?> activityJoinStatusList(String json) {
		GatewayActivityJoinStatusListReq req = JacksonUtil.decode(json, GatewayActivityJoinStatusListReq.class);
		return activityController.joinStatusList(req.getActivityType());
	}

	private ResponseEntity<?> activityReceive(String json) {
		GatewayActivityReceiveReq req = JacksonUtil.decode(json, GatewayActivityReceiveReq.class);
		return activityController.receive(req.getActivityType());
	}

	private ResponseEntity<?> queryImgBaseUrl(String json) {
		return configController.queryPath();
	}

	private ResponseEntity<?> sunshineLastPending(String json) {
		GatewaySunshineLastPendingReq req = JacksonUtil.decode(json, GatewaySunshineLastPendingReq.class);
		return sunshineClientController.last(req.getType());
	}

	private ResponseEntity<?> sunshineSubmit(String json) {
		GatewaySunshineSubmitReq req = JacksonUtil.decode(json, GatewaySunshineSubmitReq.class);
		ClientSunshineRequest realReq = new ClientSunshineRequest();
		realReq.setType(req.getType());
		realReq.setUrl(req.getUrl());
		return sunshineClientController.url(realReq);
	}

}
