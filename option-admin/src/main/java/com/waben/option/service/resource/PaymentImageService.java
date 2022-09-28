package com.waben.option.service.resource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.waben.option.common.interfacesadmin.resource.AdminPaymentImageAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.PaymentImageDTO;
import com.waben.option.common.model.request.resource.PaymentImageRequest;
import com.waben.option.mode.vo.PaymentImageVO;

@Service
public class PaymentImageService {

    @Resource
    private AdminPaymentImageAPI adminPaymentImageAPI;

    @Resource
    private ModelMapper modelMapper;

    public PageInfo<PaymentImageDTO> queryList(LocalDate day) {
        return adminPaymentImageAPI.queryList(day);
    }

    public void createToUpset(PaymentImageRequest request) {
        adminPaymentImageAPI.createToUpset(request);
    }

    public List<PaymentImageVO> clientQuery() {
        List<PaymentImageDTO> paymentImageList = adminPaymentImageAPI.query();
        List<PaymentImageVO> list = new ArrayList<>();
        if (paymentImageList.size() > 0) {
            for (PaymentImageDTO paymentImageDTO : paymentImageList) {
                PaymentImageVO paymentImageVO = modelMapper.map(paymentImageDTO,PaymentImageVO.class);
                list.add(paymentImageVO);
            }
        }
        return list;
    }

}
