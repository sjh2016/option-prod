package com.waben.option.controller.summary;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.summary.UserDataService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

//@RestController
//@RequestMapping("/userData")
//@Api(tags = {"用户数据统计"})
public class UserDataController extends AbstractBaseController {

    @Resource
    private UserDataService userDataService;

    @ApiOperation(value = "查询用户数据统计列表", response = BannerDTO.class)
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "startTime", required = false) String startTime,
                                             @RequestParam(value = "endTime", required = false) String endTime,
                                             @ApiParam(name = "page", value = "页码") int page,
                                             @ApiParam(name = "size", value = "每页数量") int size) {
        return ok(userDataService.queryList(startTime, endTime, page, size));
    }

}
