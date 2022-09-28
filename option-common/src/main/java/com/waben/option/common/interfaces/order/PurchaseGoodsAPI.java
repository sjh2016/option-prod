package com.waben.option.common.interfaces.order;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.order.PurchaseGoodsDTO;
import com.waben.option.common.model.enums.PurchaseGoodEnum;
import com.waben.option.common.model.enums.PurchaseGoodStatusEnum;
import com.waben.option.common.model.request.order.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "order-server", contextId = "PurchaseGoodsAPI", qualifier = "purchaseGoodsAPI", path = "/purchaseGoods")
public interface PurchaseGoodsAPI extends BaseAPI {

    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public Response<PageInfo<PurchaseGoodsDTO>> _queryList(@RequestParam(value = "userId", required = false) Long userId,
                                                           @RequestParam(value = "type", required = false) PurchaseGoodEnum type,
                                                           @RequestParam(value = "status", required = false) PurchaseGoodStatusEnum status,
                                                           @RequestParam("page") int page, @RequestParam("size") int size);

    @RequestMapping(value = "/createUserInfo", method = RequestMethod.POST)
    public Response<PurchaseGoodsDTO> _createUserInfo(@RequestBody CreateUserPurchaseGoodsRequest request);

    @RequestMapping(value = "/buy", method = RequestMethod.POST)
    public Response<PurchaseGoodsDTO> _buy(@RequestBody BuyUserPurchaseGoodsRequest request);

    @RequestMapping(value = "/auditGoods", method = RequestMethod.POST)
    public Response<PurchaseGoodsDTO> _auditGoods(@RequestBody AuditUserPurchaseGoodsRequest request);

    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
    public Response<PurchaseGoodsDTO> _uploadImage(@RequestBody UploadUserPurchaseGoodsImageRequest request);

    public default PageInfo<PurchaseGoodsDTO> queryList(Long userId,PurchaseGoodEnum type,PurchaseGoodStatusEnum status,int page,int size) {
        return getResponseData(_queryList(userId,type, status, page, size));
    }

    public default PurchaseGoodsDTO createUserInfo(CreateUserPurchaseGoodsRequest request) {
        return getResponseData(_createUserInfo(request));
    }

    public default PurchaseGoodsDTO buy(BuyUserPurchaseGoodsRequest request) {
        return getResponseData(_buy(request));
    }

    public default PurchaseGoodsDTO auditGoods(AuditUserPurchaseGoodsRequest request) {
        return getResponseData(_auditGoods(request));
    }

    public default PurchaseGoodsDTO uploadImage(UploadUserPurchaseGoodsImageRequest request) {
        return getResponseData(_uploadImage(request));
    }


}
