package com.waben.option.controller.resource;

import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.model.request.resource.CreateBannerRequest;
import com.waben.option.common.model.request.resource.UpdateBannerRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.mode.vo.BannerVO;
import com.waben.option.service.resource.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/banner")
@Api(tags = {"轮播图"})
public class BannerController extends AbstractBaseController {

    @Resource
    private BannerService bannerService;

    @ApiOperation(value = "查询轮播图列表", response = BannerDTO.class)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<?> queryBannerList(
            @ApiParam(name = "enable", value = "状态，是否可用") @RequestParam(value = "enable", required = false) Boolean enable,
            @RequestParam(value = "displayType", required = false) Integer displayType,
            @ApiParam(name = "page", value = "页码") int page, @ApiParam(name = "size", value = "每页数量") int size) {
        return ok(bannerService.queryBannerList(enable, displayType, page, size));
    }

    @ApiOperation(value = "查询轮播图", response = BannerDTO.class)
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public ResponseEntity<?> queryBanner(@ApiParam(name = "id", value = "轮播图id") Long id) {
        return ok(bannerService.queryBanner(id));
    }

    @ApiOperation(value = "创建轮播图", response = BannerDTO.class)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createBanner(@RequestBody CreateBannerRequest request) {
        request.setOperatorId(getCurrentUserId());
        return ok(bannerService.createBanner(request));
    }

    @ApiOperation(value = "修改轮播图", response = BannerDTO.class)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateBanner(@RequestBody UpdateBannerRequest request) {
        request.setOperatorId(getCurrentUserId());
        return ok(bannerService.updateBanner(request));
    }

    @ApiOperation(value = "删除轮播图")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteBanner(Long id) {
        bannerService.deleteBanner(id);
        return ok();
    }

    @ApiOperation(value = "查询轮播图列表", response = BannerVO.class)
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<?> queryBannerList() {
        return ok(bannerService.queryBannerList());
    }
}
