package com.waben.option.core.controller.summary;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.summary.FundDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/fundData")
public class FundDataController extends AbstractBaseController {

    @Resource
    private FundDataService fundDataService;

    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "startTime", required = false) String startTime,
                                       @RequestParam(value = "endTime", required = false) String endTime, int page, int size) {
        return ok(fundDataService.queryList(startTime,endTime,page,size));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestParam(value = "day", required = false) String day) {
        fundDataService.create(day);
        return ok();
    }

}
