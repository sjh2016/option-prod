package com.waben.option.service.resource;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waben.option.common.interfacesadmin.resource.AdminConfigAPI;

@Service
public class ConfigService {

    @Resource
    private AdminConfigAPI adminConfigAPI;

    public String queryPath() {
        return adminConfigAPI.queryPath();
    }

}
