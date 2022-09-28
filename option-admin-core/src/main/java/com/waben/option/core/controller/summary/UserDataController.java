package com.waben.option.core.controller.summary;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.summary.UserDataService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/userData")
@Api(value = "用户数据")
public class UserDataController extends AbstractBaseController {

    @Resource
    private UserDataService userDataService;

    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "startTime", required = false) String startTime,
                                       @RequestParam(value = "endTime", required = false) String endTime,int page, int size) {
        return ok(userDataService.queryList(startTime,endTime,page,size));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create() {
        userDataService.create();
        return ok();
    }

}
