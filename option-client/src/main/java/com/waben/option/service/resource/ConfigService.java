package com.waben.option.service.resource;

import com.waben.option.common.interfaces.resource.ConfigAPI;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ConfigService {

    @Resource
    private ConfigAPI configAPI;

    public String queryPath() {
        return configAPI.queryImageUrlConfig();
    }

}
