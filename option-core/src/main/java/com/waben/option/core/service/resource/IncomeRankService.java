package com.waben.option.core.service.resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.IncomeRankDTO;
import com.waben.option.common.model.request.resource.IncomeRankRequest;
import com.waben.option.data.entity.resource.IncomeRank;
import com.waben.option.data.repository.resource.IncomeRankDao;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeRankService {

    @Resource
    private IncomeRankDao incomeRankDao;

    @Resource
    private ModelMapper modelMapper;

    public PageInfo<IncomeRankDTO> queryList(String type,int page,int size) {
        QueryWrapper<IncomeRank> query = new QueryWrapper<>();
        if (type != null) {
            query = query.eq(IncomeRank.TYPE,type);
        }
        query.orderByDesc(IncomeRank.INCOME);
        PageInfo<IncomeRankDTO> pageInfo = new PageInfo<>();
        IPage<IncomeRank> incomeRankIPage = incomeRankDao.selectPage(new Page<>(page,size), query);
        if (incomeRankIPage.getTotal() > 0) {
            List<IncomeRankDTO> incomeRankList = incomeRankIPage.getRecords().stream().map(incomeRank -> modelMapper.map(incomeRank, IncomeRankDTO.class))
                    .collect(Collectors.toList());
            pageInfo.setRecords(incomeRankList);
            pageInfo.setTotal(incomeRankIPage.getTotal());
            pageInfo.setPage((int) incomeRankIPage.getPages());
            pageInfo.setSize((int) incomeRankIPage.getSize());
            return pageInfo;
        }
        return pageInfo;
    }

    public IncomeRankDTO create(IncomeRankRequest request) {
        IncomeRank incomeRank = new IncomeRank();
        incomeRank.setName(request.getName());
        incomeRank.setAmount(request.getAmount());
        incomeRank.setIncome(request.getIncome());
        incomeRank.setInviteNumber(request.getInviteNumber());
        incomeRank.setType(request.getType());
        if (StringUtils.isNotEmpty(request.getHeadImg())) {
            incomeRank.setHeadImg(request.getHeadImg());
        }
        incomeRankDao.insert(incomeRank);
        return modelMapper.map(incomeRank,IncomeRankDTO.class);
    }

    public IncomeRankDTO upset(IncomeRankRequest request) {
        IncomeRank incomeRank = incomeRankDao.selectById(request.getId());
        if (incomeRank == null) throw new ServerException(1016);
        incomeRank.setName(request.getName());
        incomeRank.setAmount(request.getAmount());
        incomeRank.setIncome(request.getIncome());
        incomeRank.setInviteNumber(request.getInviteNumber());
        incomeRank.setType(request.getType());
        if (StringUtils.isNotEmpty(request.getHeadImg())) {
            incomeRank.setHeadImg(request.getHeadImg());
        }
        incomeRankDao.updateById(incomeRank);
        return modelMapper.map(incomeRank,IncomeRankDTO.class);
    }

    public void delete(int id) {
        IncomeRank incomeRank = incomeRankDao.selectById(id);
        if (incomeRank == null) throw new ServerException(1016);
        incomeRankDao.delete(new QueryWrapper<IncomeRank>().eq(IncomeRank.ID, id));
    }

}
