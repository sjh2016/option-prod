package com.waben.option.core.service.resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.OperatorDTO;
import com.waben.option.common.model.dto.resource.RechargeDTO;
import com.waben.option.data.entity.resource.Operator;
import com.waben.option.data.entity.resource.Recharge;
import com.waben.option.data.repository.resource.RechargeDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RechargeService {

    @Resource
    private RechargeDao rechargeDao;

    @Resource
    private ModelMapper modelMapper;

    /**
     * 根据运营商获取充值金额
     * @param operatorId
     * @param page
     * @param size
     * @return
     */
    public PageInfo<RechargeDTO> queryList(Integer operatorId,int page,int size) {
        QueryWrapper<Recharge> query = new QueryWrapper<>();
        if (operatorId != null) {
            query = query.eq(Recharge.OPERATOR_ID,operatorId);
        }
        query.orderByAsc(Recharge.AMOUNT);
        PageInfo<RechargeDTO> pageInfo = new PageInfo<>();
        IPage<Recharge> rechargeIPage = rechargeDao.selectPage(new Page<>(page,size), query);
        if (rechargeIPage.getTotal() > 0) {
            List<RechargeDTO> rechargeList = rechargeIPage.getRecords().stream().map(recharge -> modelMapper.map(recharge, RechargeDTO.class))
                    .collect(Collectors.toList());
            pageInfo.setRecords(rechargeList);
            pageInfo.setTotal(rechargeIPage.getTotal());
            pageInfo.setPage((int) rechargeIPage.getPages());
            pageInfo.setSize((int) rechargeIPage.getSize());
            return pageInfo;
        }
        return pageInfo;
    }

}
