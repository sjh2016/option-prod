package com.waben.option.core.service.payment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO.PaymentMethodDTO;
import com.waben.option.common.model.dto.payment.PaymentApiConfigSimpleDTO;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.data.entity.payment.PaymentApiConfig;
import com.waben.option.data.entity.payment.PaymentPassageway;
import com.waben.option.data.repository.payment.PaymentApiConfigDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentApiConfigService {

    @Resource
    private PaymentApiConfigDao apiConfigDao;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ModelMapper modelMapper;

    /**
     * 根据id查询出入金api配置
     */
    public PaymentApiConfigDTO query(Long id) {
        PaymentApiConfig config = apiConfigDao.selectById(id);
        if (config == null) {
            throw new ServerException(2006);
        }
        PaymentApiConfigDTO result = modelMapper.map(config, PaymentApiConfigDTO.class);
        String methodJson = config.getMethodJson();
        if (methodJson != null && !"".equals(methodJson.trim())) {
            try {
                result.setMethodList(objectMapper.readValue(methodJson, new TypeReference<List<PaymentMethodDTO>>() {
                }));
            } catch (Exception e) {
                result.setMethodList(new ArrayList<>());
            }
        } else {
            result.setMethodList(new ArrayList<>());
        }
        return result;
    }

    public List<PaymentApiConfigSimpleDTO> list(List<PaymentCashType> cashType) {
        List<PaymentApiConfigSimpleDTO> result = new ArrayList<>();
        QueryWrapper<PaymentApiConfig> query = new QueryWrapper<>();
        if (cashType != null && cashType.size() > 0) {
            query.in(PaymentApiConfig.CASH_TYPE, cashType);
        }
        query.eq(PaymentPassageway.ENABLE, true);
        query.orderByDesc(PaymentPassageway.GMT_CREATE);
        List<PaymentApiConfig> list = apiConfigDao.selectList(query);
        if (list != null && list.size() > 0) {
            for (PaymentApiConfig config : list) {
                PaymentApiConfigSimpleDTO simple = modelMapper.map(config, PaymentApiConfigSimpleDTO.class);
                String methodJson = config.getMethodJson();
                if (methodJson != null && !"".equals(methodJson.trim())) {
                    try {
                        simple.setMethodList(
                                objectMapper.readValue(methodJson, new TypeReference<List<PaymentMethodDTO>>() {
                                }));
                    } catch (Exception e) {
                        simple.setMethodList(new ArrayList<>());
                    }
                } else {
                    simple.setMethodList(new ArrayList<>());
                }
                result.add(simple);
            }
        }
        return result;
    }

}
