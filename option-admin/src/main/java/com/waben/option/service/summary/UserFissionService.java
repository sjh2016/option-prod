package com.waben.option.service.summary;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waben.option.common.interfacesadmin.summary.AdminUserFissionAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.summary.UserFissionDataDTO;

@Service
public class UserFissionService {

    @Resource
    private AdminUserFissionAPI adminUserFissionAPI;

    public PageInfo<UserFissionDataDTO> queryList(String mobilePhone,int page,int size) {
        return adminUserFissionAPI.queryList(mobilePhone,page, size);
    }

}
