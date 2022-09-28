package com.waben.option.controller.resource;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.model.request.resource.MissionActivityRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.resource.MissionActivityService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/admin/missionActivity")
@Api(tags = {"任务活动"})
public class MissionActivityController  extends AbstractBaseController {

    @Resource
    private MissionActivityService missionActivityService;

    @ApiOperation(value = "查询任务活动列表", response = BannerDTO.class)
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "type", required = false) String type,
                                             @ApiParam(name = "page", value = "页码") int page,
                                             @ApiParam(name = "size", value = "每页数量") int size) {
        return ok(missionActivityService.queryList(type, page, size));
    }

    @ApiOperation(value = "创建任务活动", response = BannerDTO.class)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody MissionActivityRequest request) {
        return ok(missionActivityService.create(request));
    }

    @ApiOperation(value = "修改任务活动", response = BannerDTO.class)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> update(@RequestBody MissionActivityRequest request) {
        return ok(missionActivityService.upset(request));
    }

    @ApiOperation(value = "删除任务活动")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> delete(@RequestBody MissionActivityRequest request) {
        missionActivityService.delete(request.getId());
        return ok();
    }

}
