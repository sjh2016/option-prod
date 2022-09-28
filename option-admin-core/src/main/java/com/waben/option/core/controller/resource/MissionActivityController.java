package com.waben.option.core.controller.resource;

import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.request.resource.MissionActivityRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.MissionActivityService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/missionActivity")
@Api(value = "任务活动")
public class MissionActivityController extends AbstractBaseController {

    @Resource
    private MissionActivityService missionActivityService;

    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "type", required = false) String type, int page, int size) {
        return ok(missionActivityService.queryList(type, page, size));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> delete(@RequestParam("id") Long id) {
        missionActivityService.delete(id);
        return ok();
    }

    @RequestMapping(value = "/upset", method = RequestMethod.POST)
    public ResponseEntity<?> upset(@RequestBody MissionActivityRequest request) {
        return ok(missionActivityService.upset(request));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody MissionActivityRequest request) {
        return ok(missionActivityService.create(request));
    }

    @RequestMapping(value = "/queryByType", method = RequestMethod.GET)
    public ResponseEntity<?> queryByType(@RequestParam("type") ActivityTypeEnum type) {
        return ok(missionActivityService.queryByType(type));
    }

}
