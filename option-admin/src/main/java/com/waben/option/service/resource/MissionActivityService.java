package com.waben.option.service.resource;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waben.option.common.interfacesadmin.resource.AdminMissionActivityAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.MissionActivityDTO;
import com.waben.option.common.model.request.resource.MissionActivityRequest;

@Service
public class MissionActivityService {

    @Resource
    private AdminMissionActivityAPI adminMissionActivityAPI;

    public MissionActivityDTO create(MissionActivityRequest request) {
        return adminMissionActivityAPI.create(request);
    }

    public MissionActivityDTO upset(MissionActivityRequest request) {
        return adminMissionActivityAPI.upset(request);
    }

    public void delete(Long id) {
        adminMissionActivityAPI.delete(id);
    }

    public PageInfo<MissionActivityDTO> queryList(String type, int page, int size) {
        return adminMissionActivityAPI.queryList(type, page, size);
    }

}
