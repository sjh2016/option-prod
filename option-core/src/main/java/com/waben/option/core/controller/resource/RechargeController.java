package com.waben.option.core.controller.resource;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.RechargeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/recharge")
public class RechargeController extends AbstractBaseController {

    @Resource
    private RechargeService rechargeService;

    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "operatorId", required = false) Integer operatorId,
                                       @RequestParam(value = "page") int page,
                                       @RequestParam(value = "size") int size) {
        return ok(rechargeService.queryList(operatorId,page,size));
    }

}
