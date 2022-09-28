package com.waben.option.common.interfacesadmin.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.ConfigDTO;
import com.waben.option.common.model.dto.resource.RechargeConfigDTO;
import com.waben.option.common.model.dto.user.UserVestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: Peter
 * @date: 2021/6/24 10:32
 */
@FeignClient(value = "admin-core-server", contextId = "AdminConfigAPI", qualifier = "adminConfigAPI")
public interface AdminConfigAPI extends BaseAPI {

    @RequestMapping(method = RequestMethod.GET, value = "/config/query/path/upload")
    public Response<String> _queryUploadPathConfig();

    @RequestMapping(method = RequestMethod.GET, value = "/config/query/url/image")
    public Response<String> _queryImageUrlConfig();

    @RequestMapping(method = RequestMethod.GET, value = "/config/query/recharge")
    public Response<RechargeConfigDTO> _queryRechargeConfig();

    @RequestMapping(method = RequestMethod.GET, value = "/config/queryPath")
    public Response<String> _queryPath();

    @RequestMapping(method = RequestMethod.GET, value = "/config/query")
    public Response<ConfigDTO> _queryConfig(@RequestParam("key") String key);

    @RequestMapping(method = RequestMethod.GET, value = "/config/query/outside/broadcast/vests")
    public Response<List<UserVestDTO>> _queryOutsideBroadcastVestList();

    @RequestMapping(method = RequestMethod.GET, value = "/config/query/send/bean")
    public Response<String> _querySendBeanConfig(@RequestParam("key") String key);


    public default String querySendBeanConfig(String key) {
        return getResponseData(_querySendBeanConfig(key));
    }

    /**
     * 查询配置信息通用接口
     *
     * @param key
     * @return
     */
    public default ConfigDTO queryConfig(String key) {
        return getResponseData(_queryConfig(key));
    }

    /**
     * 查询上传文件路径配置
     */
    public default String queryUploadPathConfig() {
        return getResponseData(_queryUploadPathConfig());
    }

    /**
     * 查询图片url前缀
     */
    public default String queryImageUrlConfig() {
        return getResponseData(_queryImageUrlConfig());
    }

    /**
     * 查询充值选项和赠送 配置
     */
    public default RechargeConfigDTO queryRechargeConfig() {
        return getResponseData(_queryRechargeConfig());
    }

    public default String queryPath() {
        return getResponseData(_queryPath());
    }

    /**
     * 查询站外推送广播消息的马甲包列表
     */
    public default List<UserVestDTO> queryOutsideBroadcastVestList() {
        return getResponseData(_queryOutsideBroadcastVestList());
    }
}
