package com.waben.option.service.resource;

import com.waben.option.common.interfaces.resource.RechargeAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.RechargeDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RechargeService {

    @Resource
    private RechargeAPI rechargeAPI;

    public PageInfo<RechargeDTO> queryList(Integer operatorId,int page,int size) {
        return rechargeAPI.queryList(operatorId, page, size);
    }

}
