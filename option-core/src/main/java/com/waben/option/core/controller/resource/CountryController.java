package com.waben.option.core.controller.resource;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.CountryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/country")
public class CountryController extends AbstractBaseController {

    @Resource
    private CountryService countryService;

    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "country", required = false) String country,
                                       @RequestParam(value = "page") int page,
                                       @RequestParam(value = "size") int size) {
        return ok(countryService.queryList(country,page,size));
    }

}
