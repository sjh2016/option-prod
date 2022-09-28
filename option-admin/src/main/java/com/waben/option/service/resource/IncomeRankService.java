package com.waben.option.service.resource;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waben.option.common.interfacesadmin.resource.AdminIncomeRankAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.IncomeRankDTO;
import com.waben.option.common.model.request.resource.IncomeRankRequest;

@Service
public class IncomeRankService {

    @Resource
    private AdminIncomeRankAPI adminIncomeRankAPI;

    public IncomeRankDTO create(IncomeRankRequest request) {
        return adminIncomeRankAPI.create(request);
    }

    public IncomeRankDTO upset(IncomeRankRequest request) {
        return adminIncomeRankAPI.upset(request);
    }

    public void delete(int id) {
        adminIncomeRankAPI.delete(id);
    }

    public PageInfo<IncomeRankDTO> queryList(String type,int page, int size) {
        return adminIncomeRankAPI.queryList(type, page, size);
    }

}
