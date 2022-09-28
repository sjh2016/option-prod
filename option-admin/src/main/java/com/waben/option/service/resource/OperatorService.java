package com.waben.option.service.resource;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waben.option.common.interfacesadmin.resource.AdminOperatorAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.OperatorDTO;

@Service
public class OperatorService {

    @Resource
    private AdminOperatorAPI adminOperatorAPI;

    public PageInfo<OperatorDTO> queryList(String operator, Integer countryId, int page, int size) {
        return adminOperatorAPI.queryList(operator, countryId, page, size);
    }

}
