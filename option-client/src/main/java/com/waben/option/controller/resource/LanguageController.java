package com.waben.option.controller.resource;

import com.waben.option.common.interfaces.resource.LanguageAPI;
import com.waben.option.common.web.controller.AbstractBaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Api(tags = {"语言"})
@RestController
@RequestMapping("/language")
public class LanguageController extends AbstractBaseController {

    @Resource
    private LanguageAPI languageAPI;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<?> queryList() {
        return ok(languageAPI.queryLanguage());
    }

    @RequestMapping(value = "/queryByCode", method = RequestMethod.GET)
    public ResponseEntity<?> queryByCode(@RequestParam("code") String code) {
        return ok(languageAPI.queryByCode(code));
    }
}
