package com.waben.option.controller.resource;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.resource.LuckyDrawService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/lucky")
public class LuckyDrawController extends AbstractBaseController {

    @Resource
    private LuckyDrawService luckyDrawService;

    @RequestMapping(value = "/luckyDraw", method = RequestMethod.GET)
    public ResponseEntity<?> luckyDraw() {
        return ok(luckyDrawService.luckyDraw(getCurrentUserId()));
    }

    @RequestMapping(value = "/queryLuckyDrawCommodity", method = RequestMethod.GET)
    public ResponseEntity<?> queryLuckyDrawCommodity() {
        return ok(luckyDrawService.queryLuckyDrawCommodity());
    }

}
