package com.waben.option.service.resource;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waben.option.common.interfacesadmin.resource.AdminCountryAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.CountryDTO;

@Service
public class CountryService {

    @Resource
    private AdminCountryAPI adminCountryAPI;

    public PageInfo<CountryDTO> queryList(String country,int page,int size) {
        return adminCountryAPI.queryList(country, page, size);
    }

}
