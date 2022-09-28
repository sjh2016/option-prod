package com.waben.option.controller.resource;

import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.model.request.resource.IncomeRankRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.resource.IncomeRankService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/incomeRank")
@Api(tags = {"排行榜"})
public class IncomeRankController extends AbstractBaseController {

    @Resource
    private IncomeRankService incomeRankService;

    @ApiOperation(value = "查询排行榜列表", response = BannerDTO.class)
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryBannerList(@RequestParam(value = "type", required = false) String type,
                                             @ApiParam(name = "page", value = "页码") int page,
                                             @ApiParam(name = "size", value = "每页数量") int size) {
        return ok(incomeRankService.queryList(type, page, size));
    }

    @ApiOperation(value = "创建排行榜", response = BannerDTO.class)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createBanner(@RequestBody IncomeRankRequest request) {
        return ok(incomeRankService.create(request));
    }

    @ApiOperation(value = "修改排行榜", response = BannerDTO.class)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateBanner(@RequestBody IncomeRankRequest request) {
        return ok(incomeRankService.upset(request));
    }

    @ApiOperation(value = "删除排行榜")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteBanner(@RequestBody IncomeRankRequest request) {
        incomeRankService.delete(request.getId());
        return ok();
    }

}
