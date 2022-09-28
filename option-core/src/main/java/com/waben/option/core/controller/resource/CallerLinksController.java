package com.waben.option.core.controller.resource;

import com.waben.option.common.model.request.common.IdRequest;
import com.waben.option.common.model.request.user.CallerLinksRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.user.CallerLinksService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/caller/links")
public class CallerLinksController extends AbstractBaseController {

    @Resource
    private CallerLinksService callerLinksService;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<?> query(@RequestParam(value = "id", required = false) Long id,
                                   @RequestParam(value = "type", required = false) String type,
                                   @RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "enable", required = false) Boolean enable) {
        return ok(callerLinksService.query(id, type, name, enable));
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ResponseEntity<?> modify(@RequestBody CallerLinksRequest request) {
        callerLinksService.modify(request);
        return ok();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody CallerLinksRequest request) {
        callerLinksService.create(request);
        return ok();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> delete(@RequestBody IdRequest request) {
        callerLinksService.delete(request);
        return ok();
    }

}
