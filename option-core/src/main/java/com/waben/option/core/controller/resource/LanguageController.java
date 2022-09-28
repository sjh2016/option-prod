package com.waben.option.core.controller.resource;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.LanguageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: Peter
 * @date: 2021/6/2 16:13
 */
@RestController
@RequestMapping("/language")
public class LanguageController extends AbstractBaseController {

    @Resource
    private LanguageService languageService;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<?> queryList() {
        return ok(languageService.queryList());
    }

    @RequestMapping(value = "/queryByCode", method = RequestMethod.GET)
    public ResponseEntity<?> queryByCode(@RequestParam("code") String code) {
        return ok(languageService.queryByCode(code));
    }
}
