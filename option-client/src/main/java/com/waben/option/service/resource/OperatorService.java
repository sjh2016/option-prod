package com.waben.option.service.resource;

import com.waben.option.common.interfaces.resource.OperatorAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.OperatorDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OperatorService {

    @Resource
    private OperatorAPI operatorAPI;

    public PageInfo<OperatorDTO> queryList(String operator, Integer countryId, int page, int size) {
        return operatorAPI.queryList(operator, countryId, page, size);
    }

}
