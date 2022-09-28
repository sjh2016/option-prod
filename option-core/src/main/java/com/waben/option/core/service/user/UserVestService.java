package com.waben.option.core.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.model.dto.user.UserVestDTO;
import com.waben.option.data.entity.user.UserVest;
import com.waben.option.data.repository.user.UserVestDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserVestService {

    @Resource
    private UserVestDao dao;

    @Resource
    private IdWorker idWorker;

    @Resource
    private ModelMapper modelMapper;

    public void bind(UserVestDTO dto) {
        QueryWrapper<UserVest> query = new QueryWrapper<>();
        query.eq(UserVest.USER_ID, dto.getUserId());
        UserVest entity = dao.selectOne(query);
        if (entity != null) {
            entity.setDeviceType(dto.getDeviceType());
            entity.setVestIndex(dto.getVestIndex());
            dao.updateById(entity);
        } else {
            entity = new UserVest();
            entity.setId(idWorker.nextId());
            entity.setUserId(dto.getUserId());
            entity.setDeviceType(dto.getDeviceType());
            entity.setVestIndex(dto.getVestIndex());
            dao.insert(entity);
        }
    }

    public List<UserVestDTO> query(List<Long> userIds) {
        List<UserVestDTO> result = new ArrayList<>();
        if (userIds != null && userIds.size() > 0) {
            QueryWrapper<UserVest> query = new QueryWrapper<>();
            query.in(UserVest.USER_ID, userIds);
            List<UserVest> list = dao.selectList(query);
            if (list != null && list.size() > 0) {
                return list.stream().map(temp -> modelMapper.map(temp, UserVestDTO.class)).collect(Collectors.toList());
            }
        }
        return result;
    }

}
