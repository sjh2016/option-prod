package com.waben.option.thirdparty.controller;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.thirdparty.service.ip.IpAddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: Peter
 * @date: 2021/6/1 13:55
 */
@RestController
@RequestMapping("/ip")
public class IpAddressController extends AbstractBaseController {

    @Resource
    private IpAddressService ipAddressService;

    @RequestMapping(value = "/addressByIp", method = RequestMethod.GET)
    public ResponseEntity<?> getAddressByIp(@RequestParam("ipStr") String ipStr) {
        return ok(ipAddressService.getAddressByIp(ipStr));
    }
}
