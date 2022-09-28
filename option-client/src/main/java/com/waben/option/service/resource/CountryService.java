package com.waben.option.service.resource;

import com.waben.option.common.interfaces.resource.CountryAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.CountryDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CountryService {

    @Resource
    private CountryAPI countryAPI;

    public PageInfo<CountryDTO> queryList(String country,int page,int size) {
        return countryAPI.queryList(country, page, size);
    }

}
