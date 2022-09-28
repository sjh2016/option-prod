package com.waben.option.core.controller.resource;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.OperatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/operator")
public class OperatorController extends AbstractBaseController {

    @Resource
    private OperatorService operatorService;

    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "operator", required = false) String operator,
                                       @RequestParam(value = "countryId", required = false) Integer countryId,
                                       @RequestParam(value = "page") int page,
                                       @RequestParam(value = "size") int size) {
        return ok(operatorService.queryList(operator,countryId,page,size));
    }

}
