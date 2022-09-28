package com.waben.option.core.service.resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.InviteDTO;
import com.waben.option.common.model.dto.resource.RechargeDTO;
import com.waben.option.data.entity.resource.Invite;
import com.waben.option.data.entity.resource.Recharge;
import com.waben.option.data.repository.resource.InviteDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InviteService {

    @Resource
    private InviteDao inviteDao;

    @Resource
    private ModelMapper modelMapper;

    public PageInfo<InviteDTO> queryList() {
        QueryWrapper<Invite> query = new QueryWrapper<>();
        query.orderByAsc(Invite.NUMBER_INVITE);
        PageInfo<InviteDTO> pageInfo = new PageInfo<>();
        IPage<Invite> inviteIPage = inviteDao.selectPage(new Page<>(1,10), query);
        if (inviteIPage.getTotal() > 0) {
            List<InviteDTO> inviteList = inviteIPage.getRecords().stream().map(invite -> modelMapper.map(invite, InviteDTO.class))
                    .collect(Collectors.toList());
            pageInfo.setRecords(inviteList);
            pageInfo.setTotal(inviteIPage.getTotal());
            pageInfo.setPage((int) inviteIPage.getPages());
            pageInfo.setSize((int) inviteIPage.getSize());
            return pageInfo;
        }
        return pageInfo;
    }

}
