package com.waben.option.core.controller.summary;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.summary.UserFissionService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/userFission")
@Api(value = "裂变数据")
public class UserFissionController extends AbstractBaseController {

    @Resource
    private UserFissionService userFissionService;

    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "mobilePhone", required = false) String mobilePhone, int page, int size) {
        return ok(userFissionService.queryList(mobilePhone,page, size));
    }

}
