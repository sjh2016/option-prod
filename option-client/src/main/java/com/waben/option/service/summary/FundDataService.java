package com.waben.option.service.summary;

import com.waben.option.common.interfaces.summary.FundDataAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.summary.FundDataDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FundDataService {

    @Resource
    private FundDataAPI fundDataAPI;

    public PageInfo<FundDataDTO> queryList(String startTime, String endTime, int page, int size) {
        return fundDataAPI.queryList(startTime, endTime, page, size);
    }

}
