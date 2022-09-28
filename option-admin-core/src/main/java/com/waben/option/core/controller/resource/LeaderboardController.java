package com.waben.option.core.controller.resource;

import com.waben.option.common.model.enums.LeaderboardTypeEnum;
import com.waben.option.common.model.request.resource.LeaderboardRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: Peter
 * @date: 2021/6/23 16:37
 */
@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController extends AbstractBaseController {

    @Resource
    private LeaderboardService leaderboardService;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "type", required = false) LeaderboardTypeEnum type, @RequestParam("page") int page, @RequestParam("size") int size) {
        return ok(leaderboardService.query(type, page, size));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody LeaderboardRequest request) {
        leaderboardService.create(request);
        return ok();
    }
}
