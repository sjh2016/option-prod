package com.waben.option.service.summary;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waben.option.common.interfacesadmin.summary.AdminUserDataAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.summary.UserDataDTO;

@Service
public class UserDataService {

    @Resource
    private AdminUserDataAPI adminUserDataAPI;

    public PageInfo<UserDataDTO> queryList(String startTime,String endTime,int page,int size) {
        return adminUserDataAPI.queryList(startTime, endTime, page, size);
    }

}
