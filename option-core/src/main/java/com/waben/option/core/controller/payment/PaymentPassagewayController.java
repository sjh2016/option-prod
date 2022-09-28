package com.waben.option.core.controller.payment;

import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.request.common.SortRequest;
import com.waben.option.common.model.request.payment.PaymentPassagewayRequest;
import com.waben.option.common.model.request.payment.PaymentPassagewayUpdateEnableRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.payment.PaymentPassagewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/payment_passageway")
public class PaymentPassagewayController extends AbstractBaseController {

    @Resource
    private PaymentPassagewayService service;

    /**
     * 查询通道信息列表
     */
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public ResponseEntity<?> page(@RequestParam(value = "cashType", required = false) String cashType, int page, int size) {
        return ok(service.page(cashType, page, size));
    }

    /**
     * 新增通道信息
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody PaymentPassagewayRequest request) {
        return ok(service.create(request));
    }

    /**
     * 修改通道信息
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> update(@RequestBody PaymentPassagewayRequest request) {
        return ok(service.update(request));
    }

    /**
     * 删除通道信息
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> delete(Long id) {
        service.delete(id);
        return ok();
    }

    /**
     * 根据id查询通道信息
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<?> query(Long id) {
        return ok(service.query(id));
    }

    /**
     * 上线/下线
     */
    @RequestMapping(value = "/updateEnable", method = RequestMethod.POST)
    public ResponseEntity<?> updateEnable(@RequestBody PaymentPassagewayUpdateEnableRequest request) {
        service.updateEnable(request);
        return ok();
    }

    /**
     * 置顶
     */
    @RequestMapping(value = "/topping", method = RequestMethod.POST)
    public ResponseEntity<?> topping(Long id) {
        service.topping(id);
        return ok();
    }

    /**
     * 排序
     */
    @RequestMapping(value = "/updateSort", method = RequestMethod.POST)
    public ResponseEntity<?> updateSort(@RequestBody SortRequest request) {
        service.updateSort(request);
        return ok();
    }

    /**
     * 前端显示通道列表
     */
    @RequestMapping(value = "/queryDisplayList", method = RequestMethod.POST)
    public ResponseEntity<?> queryDisplayList(@RequestParam(value = "cashTypes", required = false) PaymentCashType cashTypes) {
        return ok(service.queryDisplayList(cashTypes));
    }

}
