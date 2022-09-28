package com.waben.option.service.summary;

import com.waben.option.common.interfaces.summary.UserFissionAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.summary.UserFissionDataDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserFissionService {

    @Resource
    private UserFissionAPI userFissionAPI;

    public PageInfo<UserFissionDataDTO> queryList(String mobilePhone,int page,int size) {
        return userFissionAPI.queryList(mobilePhone,page, size);
    }

}
