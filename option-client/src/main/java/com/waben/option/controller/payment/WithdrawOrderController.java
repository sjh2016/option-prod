package com.waben.option.controller.payment;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfaces.account.AccountAPI;
import com.waben.option.common.interfaces.thirdparty.PaymentOrderAPI;
import com.waben.option.common.interfaces.thirdparty.WithdrawOrderAPI;
import com.waben.option.common.interfaces.user.UserAPI;
import com.waben.option.common.model.request.payment.WithdrawOtcFrontRequest;
import com.waben.option.common.model.request.payment.WithdrawUserPageRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

@Api(tags = {"提现订单"})
@RestController
@RequestMapping("/withdraw_order")
public class WithdrawOrderController extends AbstractBaseController {

    @Resource
    private WithdrawOrderAPI withdrawOrderAPI;

    @Resource
    private PaymentOrderAPI paymentOrderAPI;

    @Resource
    private AccountAPI accountAPI;

    @Resource
    private UserAPI userAPI;

    @RequestMapping(value = "/place_otc_order", method = RequestMethod.POST)
    public ResponseEntity<?> placeOtcOrder(@RequestBody WithdrawOtcFrontRequest request) {
        withdrawOrderAPI.placeOtcOrder(getCurrentUserId(), request);
        return ok();
    }

//    @RequestMapping(value = "/admin/page", method = RequestMethod.GET)
//    public ResponseEntity<?> adminPage(WithdrawAdminPageRequest adminReq) {
//        PageInfo<WithdrawOrderDTO> adminPage = withdrawOrderAPI.adminPage(adminReq);
//        if (adminPage.getTotal() > 0) {
//            List<Long> uidList = new ArrayList<>();
//            List<WithdrawOrderDTO> orderList = adminPage.getRecords();
//            for (WithdrawOrderDTO order : orderList) {
//                if (!uidList.contains(order.getUserId())) {
//                    uidList.add(order.getUserId());
//                }
//            }
//            List<WithdrawAmountDTO> withdrawAmountList = withdrawOrderAPI.totalWithdrawAmountByUsers(uidList);
//            List<WithdrawAmountDTO> paymentList = paymentOrderAPI.inviteRechargePeopleByUsers(uidList);
//            List<UserDTO> userDTOList = userAPI.queryUserList(uidList);
//            List<UserWithdrawSummaryDTO> summaryDTOList = accountAPI.userWithdrawSummaryByUsers(uidList);
//            for (WithdrawOrderDTO withdrawOrder : orderList) {
//                if (!CollectionUtils.isEmpty(userDTOList)) {
//                    for (UserDTO userDTO : userDTOList) {
//                        if (userDTO.getId().compareTo(withdrawOrder.getUserId()) == 0) {
//                            withdrawOrder.setUsername(userDTO.getUsername());
//                            withdrawOrder.setNickname(userDTO.getName());
//                            break;
//                        }
//                    }
//                }
//                if (!CollectionUtils.isEmpty(withdrawAmountList)) {
//                    for (WithdrawAmountDTO withdrawAmountDTO : withdrawAmountList) {
//                        if (withdrawAmountDTO.getUserId().compareTo(withdrawOrder.getUserId()) == 0) {
//                            withdrawOrder.setTotalWithdrawAmount(withdrawAmountDTO.getAmount());
//                            break;
//                        }
//                    }
//                }
//                if (!CollectionUtils.isEmpty(paymentList)) {
//                    for (WithdrawAmountDTO payment : paymentList) {
//                        if (payment.getUserId().compareTo(withdrawOrder.getUserId()) == 0) {
//                            withdrawOrder.setTotalRechargeAmount(payment.getAmount());
//                            break;
//                        }
//                    }
//                }
//                if (!CollectionUtils.isEmpty(summaryDTOList)) {
//                    for (UserWithdrawSummaryDTO summaryDTO : summaryDTOList) {
//                        if (withdrawOrder.getUserId().compareTo(summaryDTO.getUserId()) == 0) {
//                            withdrawOrder.setTotalInviteAmount(summaryDTO.getTotalInviteAmount());
//                            withdrawOrder.setTotalLoginAmount(summaryDTO.getTotalLoginAmount());
//                            withdrawOrder.setTotalDivideAmount(summaryDTO.getTotalDivideAmount());
//                            break;
//                        }
//                    }
//                }
//                withdrawOrder.setInvitePeople(userAPI.invitePeopleByUsers(withdrawOrder.getBrokerSymbol()));
//                withdrawOrder.setInviteRechargePeople(paymentOrderAPI.inviteRechargePeopleBySymbol(withdrawOrder.getBrokerSymbol()));
//            }
//        }
//        return ok(adminPage);
//    }

    @RequestMapping(value = "/user/page", method = RequestMethod.GET)
    public ResponseEntity<?> userPage(WithdrawUserPageRequest req) {
        return ok(withdrawOrderAPI.userPage(getCurrentUserId(), req));
    }

//    @RequestMapping(value = "/admin/sta", method = RequestMethod.GET)
//    public ResponseEntity<?> adminSta(WithdrawAdminPageRequest adminReq) {
//        return ok(withdrawOrderAPI.adminSta(adminReq));
//    }
//
//    @RequestMapping(value = "/underline/successful", method = RequestMethod.POST)
//    public ResponseEntity<?> underlineSuccessful(@RequestBody WithdrawUnderlineSuccessfulRequest request) {
//        withdrawOrderAPI.underlineSuccessful(getCurrentUserId(), request);
//        return ok();
//    }
//
//    @RequestMapping(value = "/underline/notpass", method = RequestMethod.POST)
//    public ResponseEntity<?> underlineNotpass(@RequestBody WithdrawUnderlineNotpassRequest request) {
//        withdrawOrderAPI.underlineNotpass(getCurrentUserId(), request);
//        return ok();
//    }
//
//    @RequestMapping(value = "/system/process", method = RequestMethod.POST)
//    public ResponseEntity<?> systemProcess(@RequestBody WithdrawSystemProcessRequest request) {
//        log.info("withdraw process ip:{}",getUserIp());
//        withdrawOrderAPI.systemProcess(getCurrentUserId(), request);
//        return ok();
//    }
    
    @RequestMapping(value = "/isWithdrawTime", method = RequestMethod.GET)
    public ResponseEntity<?> isWithdrawTime() {
        return ok(withdrawOrderAPI.isWithdrawTime());
    }

}
