package com.waben.option.controller.order;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfaces.order.PurchaseGoodsAPI;
import com.waben.option.common.model.enums.PurchaseGoodEnum;
import com.waben.option.common.model.enums.PurchaseGoodStatusEnum;
import com.waben.option.common.model.request.order.BuyUserPurchaseGoodsRequest;
import com.waben.option.common.model.request.order.CreateUserPurchaseGoodsRequest;
import com.waben.option.common.model.request.order.UploadUserPurchaseGoodsImageRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

@Api(tags = {"商品预购"})
@RestController
@RequestMapping("/purchaseGoods")
public class PurchaseGoodsController extends AbstractBaseController {

    @Resource
    private PurchaseGoodsAPI purchaseGoodsAPI;

    @RequestMapping(value = "/admin/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "userId", required = false) Long userId,
                                       @RequestParam(value = "type", required = false) PurchaseGoodEnum type,
                                       @RequestParam(value = "status", required = false) PurchaseGoodStatusEnum status,
                                       @RequestParam("page") int page, @RequestParam("size") int size) {
        return ok(purchaseGoodsAPI.queryList(userId,type, status, page, size));
    }

    @RequestMapping(value = "/client/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryPage(@RequestParam(value = "type", required = false) Long userId,
                                       @RequestParam(value = "type", required = false) PurchaseGoodEnum type,
                                       @RequestParam(value = "status", required = false) PurchaseGoodStatusEnum status) {
        return ok(purchaseGoodsAPI.queryList(getCurrentUserId(),type, status, 1, 10));
    }

    @RequestMapping(value = "/createUserInfo", method = RequestMethod.POST)
    public ResponseEntity<?> createUserInfo(@RequestBody CreateUserPurchaseGoodsRequest request) {
        request.setUserId(getCurrentUserId());
        return ok(purchaseGoodsAPI.createUserInfo(request));
    }

    @RequestMapping(value = "/buy", method = RequestMethod.POST)
    public ResponseEntity<?> buy(@RequestBody BuyUserPurchaseGoodsRequest request) {
        request.setUserId(getCurrentUserId());
        return ok(purchaseGoodsAPI.buy(request));
    }

//    @RequestMapping(value = "/admin/auditGoods", method = RequestMethod.POST)
//    public ResponseEntity<?> auditGoods(@RequestBody AuditUserPurchaseGoodsRequest request) {
//        return ok(purchaseGoodsAPI.auditGoods(request));
//    }

    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
    public ResponseEntity<?> uploadImage(@RequestBody UploadUserPurchaseGoodsImageRequest request) {
        request.setUserId(getUserId());
        return ok(purchaseGoodsAPI.uploadImage(request));
    }

}
