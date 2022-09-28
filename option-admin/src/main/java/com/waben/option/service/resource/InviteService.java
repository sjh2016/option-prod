package com.waben.option.service.resource;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waben.option.common.interfacesadmin.resource.AdminInviteAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.InviteDTO;

@Service
public class InviteService {

    @Resource
    private AdminInviteAPI adminInviteAPI;

    public PageInfo<InviteDTO> queryList() {
        return adminInviteAPI.queryList();
    }

}
