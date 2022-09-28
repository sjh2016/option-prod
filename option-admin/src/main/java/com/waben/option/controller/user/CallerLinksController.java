package com.waben.option.controller.user;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfacesadmin.user.AdminCallerLinksAPI;
import com.waben.option.common.model.request.common.IdRequest;
import com.waben.option.common.model.request.user.CallerLinksRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

@RestController
@RequestMapping("/")
public class CallerLinksController extends AbstractBaseController {

    @Resource
    private AdminCallerLinksAPI adminCallerLinksAPI;

    @RequestMapping(value = "/admin/caller/links/query", method = RequestMethod.GET)
    public ResponseEntity<?> query(@RequestParam(value = "id", required = false) Long id,
                                   @RequestParam(value = "type", required = false) String type,
                                   @RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "enable", required = false) Boolean enable) {
        return ok(adminCallerLinksAPI.query(id, type, name, enable));
    }

    @RequestMapping(value = "/client/caller/links/query", method = RequestMethod.GET)
    public ResponseEntity<?> clientQuery(@RequestParam(value = "id", required = false) Long id,
                                         @RequestParam(value = "type", required = false) String type,
                                         @RequestParam(value = "name", required = false) String name) {
        return ok(adminCallerLinksAPI.query(id, type, name, Boolean.TRUE));
    }

    @RequestMapping(value = "/admin/caller/links/modify", method = RequestMethod.POST)
    public ResponseEntity<?> modify(@RequestBody CallerLinksRequest request) {
        adminCallerLinksAPI.modify(request);
        return ok();
    }

    @RequestMapping(value = "/admin/caller/links/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody CallerLinksRequest request) {
        adminCallerLinksAPI.create(request);
        return ok();
    }

    @RequestMapping(value = "/admin/caller/links/delete", method = RequestMethod.POST)
    public ResponseEntity<?> delete(@RequestBody IdRequest request) {
        adminCallerLinksAPI.delete(request);
        return ok();
    }

}
