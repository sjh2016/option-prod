package com.waben.option.core.controller;

import com.waben.option.common.model.enums.PurchaseGoodEnum;
import com.waben.option.common.model.enums.PurchaseGoodStatusEnum;
import com.waben.option.common.model.request.order.*;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.order.PurchaseGoodsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/purchaseGoods")
public class PurchaseGoodsController extends AbstractBaseController {

    @Resource
    private PurchaseGoodsService purchaseGoodsService;

    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "userId", required = false) Long userId,
                                       @RequestParam(value = "type", required = false) PurchaseGoodEnum type,
                                       @RequestParam(value = "status", required = false) PurchaseGoodStatusEnum status,
                                       @RequestParam("page") int page, @RequestParam("size") int size) {
        return ok(purchaseGoodsService.queryList(userId,type, status, page, size));
    }

    @RequestMapping(value = "/createUserInfo", method = RequestMethod.POST)
    public ResponseEntity<?> createUserInfo(@RequestBody CreateUserPurchaseGoodsRequest request) {
        return ok(purchaseGoodsService.createUserInfo(request));
    }

    @RequestMapping(value = "/buy", method = RequestMethod.POST)
    public ResponseEntity<?> buy(@RequestBody BuyUserPurchaseGoodsRequest request) {
        purchaseGoodsService.buy(request);
        return ok();
    }

    @RequestMapping(value = "/auditGoods", method = RequestMethod.POST)
    public ResponseEntity<?> auditGoods(@RequestBody AuditUserPurchaseGoodsRequest request) {
        purchaseGoodsService.auditGoods(request);
        return ok();
    }

    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
    public ResponseEntity<?> uploadImage(@RequestBody UploadUserPurchaseGoodsImageRequest request) {
        purchaseGoodsService.uploadImage(request);
        return ok();
    }


}
