package com.waben.option.service.resource;

import com.waben.option.common.interfaces.resource.MissionActivityAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.MissionActivityDTO;
import com.waben.option.common.model.request.resource.MissionActivityRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MissionActivityService {

    @Resource
    private MissionActivityAPI missionActivityAPI;

    public MissionActivityDTO create(MissionActivityRequest request) {
        return missionActivityAPI.create(request);
    }

    public MissionActivityDTO upset(MissionActivityRequest request) {
        return missionActivityAPI.upset(request);
    }

    public void delete(Long id) {
        missionActivityAPI.delete(id);
    }

    public PageInfo<MissionActivityDTO> queryList(String type, int page, int size) {
        return missionActivityAPI.queryList(type, page, size);
    }

}
