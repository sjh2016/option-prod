package com.waben.option.service.resource;

import com.waben.option.common.interfaces.resource.PaymentImageAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.PaymentImageDTO;
import com.waben.option.common.model.request.resource.PaymentImageRequest;
import com.waben.option.mode.vo.PaymentImageVO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentImageService {

    @Resource
    private PaymentImageAPI paymentImageAPI;

    @Resource
    private ModelMapper modelMapper;

    public PageInfo<PaymentImageDTO> queryList(LocalDate day) {
        return paymentImageAPI.queryList(day);
    }

    public void createToUpset(PaymentImageRequest request) {
        paymentImageAPI.createToUpset(request);
    }

    public List<PaymentImageVO> clientQuery() {
        List<PaymentImageDTO> paymentImageList = paymentImageAPI.query();
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
