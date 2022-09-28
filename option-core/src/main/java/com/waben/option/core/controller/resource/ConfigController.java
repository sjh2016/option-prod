package com.waben.option.core.controller.resource;

import com.waben.option.common.constants.DBConstants;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.ConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: Peter
 * @date: 2021/6/24 10:34
 */
@RestController
@RequestMapping("config")
public class ConfigController extends AbstractBaseController {

    @Resource
    private ConfigService configService;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<?> queryConfig(@RequestParam("key") String key) {
        return ok(configService.queryConfig(key));
    }

    /**
     * 查询上传文件路径配置
     */
    @RequestMapping(value = "/query/path/upload", method = RequestMethod.GET)
    public ResponseEntity<?> queryUploadPathConfig() {
        return ok(configService.queryConfig(DBConstants.CONFIG_PATH_UPLOAD_KEY).getValue());
    }

    /**
     * 查询图片url前缀
     */
    @RequestMapping(value = "/query/url/image", method = RequestMethod.GET)
    public ResponseEntity<?> queryUrlConfig() {
        return ok(configService.queryConfig(DBConstants.CONFIG_URL_IMAGE_KEY).getValue());
    }

    /**
     * 获取充值配置
     */
    @RequestMapping(value = "/query/recharge", method = RequestMethod.GET)
    public ResponseEntity<?> queryRechargeConfig() {
        return ok(configService.queryRechargeConfig());
    }

    @RequestMapping(value = "/query/queryPath", method = RequestMethod.GET)
    public ResponseEntity<?> queryPath() {
        return ok(configService.queryPath());
    }


    /**
     * 查询站外推送广播消息的马甲包列表
     */
    @RequestMapping(value = "/query/outside/broadcast/vests", method = RequestMethod.GET)
    public ResponseEntity<?> queryOutsideBroadcastVestList() {
        return ok(configService.queryOutsideBroadcastVestList());
    }

    /**
     * 查询上传文件路径配置
     */
    @RequestMapping(value = "/query/send/bean", method = RequestMethod.GET)
    public ResponseEntity<?> querySendBeanConfig(String key) {
        return ok(configService.queryConfig(key).getValue());
    }
}
