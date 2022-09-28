package com.waben.option.service.summary;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waben.option.common.interfacesadmin.summary.AdminFundDataAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.summary.FundDataDTO;

@Service
public class FundDataService {

    @Resource
    private AdminFundDataAPI adminFundDataAPI;

    public PageInfo<FundDataDTO> queryList(String startTime, String endTime, int page, int size) {
        return adminFundDataAPI.queryList(startTime, endTime, page, size);
    }

}
