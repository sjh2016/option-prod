package com.waben.option.core.controller.resource;

import com.waben.option.common.model.request.resource.CreateBannerRequest;
import com.waben.option.common.model.request.resource.UpdateBannerRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.BannerService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/banner")
@Api(value = "轮播图接口")
public class BannerController extends AbstractBaseController {

    @Resource
    private BannerService bannerService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<?> queryBannerList(@RequestParam(value = "enable", required = false) Boolean enable,
                                             @RequestParam(value = "displayType", required = false) Integer displayType, int page, int size) {
        return ok(bannerService.queryBannerList(enable, displayType, page, size));
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<?> queryById(@RequestParam("id") Long id) {
        return ok(bannerService.queryById(id));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@RequestParam("id") Long id) {
        bannerService.deleteBanner(id);
        return ok();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> update(@RequestBody UpdateBannerRequest request) {
        return ok(bannerService.updateBanner(request));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody CreateBannerRequest request) {
        return ok(bannerService.createBanner(request));
    }

}
