package com.waben.option.service.summary;

import com.waben.option.common.interfaces.summary.UserDataAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.summary.UserDataDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserDataService {

    @Resource
    private UserDataAPI userDataAPI;

    public PageInfo<UserDataDTO> queryList(String startTime,String endTime,int page,int size) {
        return userDataAPI.queryList(startTime, endTime, page, size);
    }

}
