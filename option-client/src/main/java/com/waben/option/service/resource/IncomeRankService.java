package com.waben.option.service.resource;

import com.waben.option.common.interfaces.resource.IncomeRankAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.IncomeRankDTO;
import com.waben.option.common.model.request.resource.IncomeRankRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class IncomeRankService {

    @Resource
    private IncomeRankAPI incomeRankAPI;

    public IncomeRankDTO create(IncomeRankRequest request) {
        return incomeRankAPI.create(request);
    }

    public IncomeRankDTO upset(IncomeRankRequest request) {
        return incomeRankAPI.upset(request);
    }

    public void delete(int id) {
        incomeRankAPI.delete(id);
    }

    public PageInfo<IncomeRankDTO> queryList(String type,int page, int size) {
        return incomeRankAPI.queryList(type, page, size);
    }

}
