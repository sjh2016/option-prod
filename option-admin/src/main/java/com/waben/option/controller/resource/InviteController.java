package com.waben.option.controller.resource;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.resource.InviteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = {"奖励配置"})
@RestController
@RequestMapping("/invite")
public class InviteController extends AbstractBaseController {

    @Resource
    private InviteService inviteService;

    @ApiOperation(value = "获取奖励配置列表")
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList() {
        return ok(inviteService.queryList());
    }

}
