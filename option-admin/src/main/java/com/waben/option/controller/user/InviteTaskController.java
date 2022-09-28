package com.waben.option.controller.user;

import java.time.LocalDate;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfacesadmin.user.AdminInviteTaskAPI;
import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.model.enums.InviteAuditStatusEnum;
import com.waben.option.common.model.request.user.InviteTaskAuditRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/invite/task")
@Api(tags = {"邀请奖励审核"})
public class InviteTaskController extends AbstractBaseController {

    @Resource
    private AdminInviteTaskAPI adminInviteTaskAPI;

    @ApiOperation(value = "查看审核邀请注册奖励", response = BannerDTO.class)
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "status", required = false) InviteAuditStatusEnum status,
                                       @RequestParam(value = "day", required = false) LocalDate day,
                                       @RequestParam(value = "uidList", required = false) List<Long> uidList,
                                       @RequestParam("page") int page, @RequestParam("size") int size,
                                       @RequestParam(value = "topId",required = false) String topId) {
        return ok(adminInviteTaskAPI.queryList(status, day, uidList, page, size,topId));
    }

    @ApiOperation(value = "系统审核邀请注册奖励", response = BannerDTO.class)
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public ResponseEntity<?> audit(@RequestBody InviteTaskAuditRequest request) {
        adminInviteTaskAPI.audit(request);
        return ok();
    }
}
