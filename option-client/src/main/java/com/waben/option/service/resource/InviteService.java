package com.waben.option.service.resource;

import com.waben.option.common.interfaces.resource.InviteAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.InviteDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class InviteService {

    @Resource
    private InviteAPI inviteAPI;

    public PageInfo<InviteDTO> queryList() {
        return inviteAPI.queryList();
    }

}
