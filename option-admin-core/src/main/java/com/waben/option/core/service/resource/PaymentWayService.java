package com.waben.option.core.service.resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.PaymentWayDTO;
import com.waben.option.common.model.dto.resource.RechargeDTO;
import com.waben.option.data.entity.resource.PaymentWay;
import com.waben.option.data.entity.resource.Recharge;
import com.waben.option.data.repository.resource.PaymentWayDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentWayService {

    @Resource
    private PaymentWayDao paymentWayDao;

    @Resource
    private ModelMapper modelMapper;

    public PageInfo<PaymentWayDTO> queryList() {
        PageInfo<PaymentWayDTO> pageInfo = new PageInfo<>();
        IPage<PaymentWay> paymentWayIPage = paymentWayDao.selectPage(new Page<>(), new QueryWrapper<>());
        if (paymentWayIPage.getTotal() > 0) {
            List<PaymentWayDTO> paymentWayList = paymentWayIPage.getRecords().stream().map(paymentWay -> modelMapper.map(paymentWay, PaymentWayDTO.class))
                    .collect(Collectors.toList());
            pageInfo.setRecords(paymentWayList);
            pageInfo.setTotal(paymentWayIPage.getTotal());
            pageInfo.setPage((int) paymentWayIPage.getPages());
            pageInfo.setSize((int) paymentWayIPage.getSize());
            return pageInfo;
        }
        return pageInfo;
    }

}
