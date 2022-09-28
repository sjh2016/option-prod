package com.waben.option.core.controller.resource;

import com.waben.option.common.model.request.resource.IncomeRankRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.IncomeRankService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/incomeRank")
@Api(value = "排行榜数据")
public class IncomeRankController extends AbstractBaseController {

    @Resource
    private IncomeRankService incomeRankService;

    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "type", required = false) String type, int page, int size) {
        return ok(incomeRankService.queryList(type, page, size));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> delete(@RequestParam("id") int id) {
        incomeRankService.delete(id);
        return ok();
    }

    @RequestMapping(value = "/upset", method = RequestMethod.POST)
    public ResponseEntity<?> upset(@RequestBody IncomeRankRequest request) {
        return ok(incomeRankService.upset(request));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody IncomeRankRequest request) {
        return ok(incomeRankService.create(request));
    }

}
