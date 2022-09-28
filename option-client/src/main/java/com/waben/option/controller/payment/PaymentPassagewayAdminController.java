package com.waben.option.controller.payment;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.PaymentPassagewayAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.payment.PaymentPassagewayDTO;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.request.common.IdRequest;
import com.waben.option.common.model.request.common.SortRequest;
import com.waben.option.common.model.request.payment.PaymentPassagewayRequest;
import com.waben.option.common.model.request.payment.PaymentPassagewayUpdateEnableRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.ApiOperation;

/**
 * 支付通道信息接口
 */
//@Api(tags = {"支付通道"})
//@RestController
//@RequestMapping("/admin/payment_passageway")
public class PaymentPassagewayAdminController extends AbstractBaseController {

    @Resource
    private PaymentPassagewayAPI paymentPassagewayAPI;

    /**
     * 分页获取数据
     */
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public ResponseEntity<?> query(@RequestParam(value = "cashType", required = false) String cashType, int page, int size) {
        PageInfo<PaymentPassagewayDTO> pageInfo = paymentPassagewayAPI.page(cashType, page, size);
//        List<PaymentPassagewayDTO> list = pageInfo.getRecords();
//        if (list != null && list.size() > 0) {
//            String locale = LocaleContext.get();
//            for (PaymentPassagewayDTO dto : list) {
//                dto.setDisplayName(dto.getLanguageDisplayName(locale));
//                dto.setDescription(dto.getLanguageDescription(locale));
//            }
//        }
        return ok(pageInfo);
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<Response<PaymentPassagewayDTO>> create(@RequestBody PaymentPassagewayRequest request) {
        return ok(paymentPassagewayAPI.create(request));
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<Response<PaymentPassagewayDTO>> update(@RequestBody PaymentPassagewayRequest request) {
        return ok(paymentPassagewayAPI.update(request));
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<Response<Void>> delete(@RequestBody IdRequest request) {
        if (request == null || request.getId() == null) {
            throw new ServerException(1001);
        }
        paymentPassagewayAPI.delete(request.getId());
        return ok();
    }

    /**
     * 查询
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<?> query(@RequestBody IdRequest request) {
        if (request == null || request.getId() == null) {
            throw new ServerException(1001);
        }
        return ok(paymentPassagewayAPI.query(request.getId()));
    }

    /**
     * 上线/下线
     */
    @RequestMapping(value = "/updateEnable", method = RequestMethod.POST)
    public ResponseEntity<Response<Void>> updateEnable(@RequestBody PaymentPassagewayUpdateEnableRequest request) {
        paymentPassagewayAPI.updateEnable(request);
        return ok();
    }

    /**
     * 置顶
     */
    @RequestMapping(value = "/topping", method = RequestMethod.POST)
    public ResponseEntity<Response<Void>> topping(@RequestBody IdRequest request) {
        if (request == null || request.getId() == null) {
            throw new ServerException(1001);
        }
        paymentPassagewayAPI.topping(request.getId());
        return ok();
    }

    /**
     * 排序
     */
    @RequestMapping(value = "/updateSort", method = RequestMethod.POST)
    public ResponseEntity<Response<Void>> updateSort(@RequestBody SortRequest request) {
        if (request == null || request.getId() == null || request.getSort() == null) {
            throw new ServerException(1001);
        }
        paymentPassagewayAPI.updateSort(request);
        return ok();
    }

    /**
     * 通道列表
     */
    @ApiOperation("查询通道列表")
    @RequestMapping(value = "/queryDisplayList", method = RequestMethod.GET)
    public ResponseEntity<?> queryDisplayList(String cashTypes) {
        return ok(paymentPassagewayAPI.queryDisplayList(PaymentCashType.valueOf(cashTypes)));
    }

}
