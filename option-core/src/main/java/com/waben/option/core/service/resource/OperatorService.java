package com.waben.option.core.service.resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.OperatorDTO;
import com.waben.option.data.entity.resource.Operator;
import com.waben.option.data.repository.resource.OperatorDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperatorService {

    @Resource
    private OperatorDao operatorDao;

    @Resource
    private ModelMapper modelMapper;

    /**
     * 根据国家获取运营商
     * @param operatorName
     * @param countryId
     * @param page
     * @param size
     * @return
     */
    public PageInfo<OperatorDTO> queryList(String operatorName,Integer countryId,int page,int size) {
        QueryWrapper<Operator> query = new QueryWrapper<>();
        if (operatorName != null) {
            query = query.eq(Operator.OPERATOR,operatorName);
        }
        if (countryId != null) {
            query = query.eq(Operator.COUNTRY_ID,countryId);
        }
        query.orderByAsc(Operator.ID);
        PageInfo<OperatorDTO> pageInfo = new PageInfo<>();
        IPage<Operator> operatorIPage = operatorDao.selectPage(new Page<>(page,size), query);
        if (operatorIPage.getTotal() > 0) {
            List<OperatorDTO> operatorList = operatorIPage.getRecords().stream().map(operator -> modelMapper.map(operator, OperatorDTO.class))
                    .collect(Collectors.toList());
            pageInfo.setRecords(operatorList);
            pageInfo.setTotal(operatorIPage.getTotal());
            pageInfo.setPage((int) operatorIPage.getPages());
            pageInfo.setSize((int) operatorIPage.getSize());
            return pageInfo;
        }
        return pageInfo;
    }

}
