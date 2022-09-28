package com.waben.option.core.service.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.component.LocaleContext;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.payment.PaymentPassagewayDTO;
import com.waben.option.common.model.dto.payment.PaymentPassagewayDTO.ExchangeRateDTO;
import com.waben.option.common.model.dto.payment.PaymentPassagewayDTO.PaymentPassagewayLanguageDTO;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.request.common.SortRequest;
import com.waben.option.common.model.request.payment.PaymentPassagewayRequest;
import com.waben.option.common.model.request.payment.PaymentPassagewayUpdateEnableRequest;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.data.entity.payment.PaymentApiConfig;
import com.waben.option.data.entity.payment.PaymentApiConfig.PaymentMehtod;
import com.waben.option.data.entity.payment.PaymentPassageway;
import com.waben.option.data.repository.payment.PaymentApiConfigDao;
import com.waben.option.data.repository.payment.PaymentPassagewayDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentPassagewayService {

    @Resource
    private PaymentPassagewayDao passagewayDao;

    @Resource
    private PaymentApiConfigDao apiConfigDao;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private IdWorker idWorker;

    /**
     * 获取通道信息列表
     */
    public PageInfo<PaymentPassagewayDTO> page(String cashType, int page, int size) {
        QueryWrapper<PaymentPassageway> query = new QueryWrapper<>();
        if (!StringUtils.isEmpty(cashType)) {
            query.eq(PaymentPassageway.CASH_TYPE, PaymentCashType.valueOf(cashType));
        }
        query.orderByAsc(PaymentPassageway.SORT).orderByDesc(PaymentPassageway.GMT_CREATE);
        IPage<PaymentPassageway> pageWrapper = passagewayDao.selectPage(new Page<>(page, size), query);
        PageInfo<PaymentPassagewayDTO> pageInfo = new PageInfo<>();
        pageInfo.setRecords(converter(pageWrapper.getRecords()));
        pageInfo.setPage((int) pageWrapper.getPages());
        pageInfo.setSize((int) pageWrapper.getSize());
        pageInfo.setTotal(pageWrapper.getTotal());
        return pageInfo;
    }

    /**
     * 新增通道信息
     */
    public PaymentPassagewayDTO create(PaymentPassagewayRequest request) {
        if (request.getPayApiId() == null || request.getPayMethodId() == null) {
            throw new ServerException(1001);
        }
        PaymentApiConfig config = apiConfigDao.selectById(request.getPayApiId());
        if (config == null) {
            throw new ServerException(1001);
        }

        PaymentPassageway passageway = new PaymentPassageway();
        passageway.setId(idWorker.nextId());
        passageway.setPayApiId(request.getPayApiId());
        passageway.setPayMethodId(request.getPayMethodId());
        passageway.setCashType(config.getCashType());
        passageway.setMinAmount(request.getMinAmount());
        passageway.setMaxAmount(request.getMaxAmount());
        passageway.setLogo(request.getLogo());
        passageway.setCountry(request.getCountry());
        passageway.setSelection(request.getSelection());
        if (!StringUtils.isBlank(request.getCountry()) && request.getCountry().indexOf("ALL") >= 0) {
            passageway.setCountry("ALL");
        }
        passageway.setEnable(false);
        passageway.setSort(999);
        passageway.setNeedKyc(request.getNeedKyc() != null ? request.getNeedKyc() : false);
        if (request.getExchangeRateList() != null) {
            checkExchangeRateList(request.getExchangeRateList());
            passageway.setExchangeRateJson(JacksonUtil.encode(request.getExchangeRateList()));
        } else {
            passageway.setExchangeRateJson("[]");
        }
        if (request.getLanguageList() != null) {
            passageway.setLanguageJson(JacksonUtil.encode(request.getLanguageList()));
        } else {
            passageway.setLanguageJson("[]");
        }
        passageway.setGmtCreate(LocalDateTime.now());
        passagewayDao.insert(passageway);
        PaymentPassagewayDTO result = modelMapper.map(passageway, PaymentPassagewayDTO.class);
        result.setExchangeRateList(request.getExchangeRateList());
        return result;
    }

    private void checkExchangeRateList(List<ExchangeRateDTO> exchangeRateList) {
        for (ExchangeRateDTO rate : exchangeRateList) {
            if (StringUtils.isBlank(rate.getCurrency()) || rate.getExchangeRate() == null
                    || rate.getExchangeRate().compareTo(BigDecimal.ZERO) < 0) {
                throw new ServerException(1001);
            }
        }
    }

    /**
     * 修改通道信息
     */
    public PaymentPassagewayDTO update(PaymentPassagewayRequest request) {
        PaymentPassageway passageway = passagewayDao.selectById(request.getId());
        if (passageway == null) {
            throw new ServerException(2006);
        }
        if (request.getPayApiId() == null || request.getPayMethodId() == null) {
            throw new ServerException(2006);
        }
        PaymentApiConfig config = apiConfigDao.selectById(request.getPayApiId());
        if (config == null) {
            throw new ServerException(2006);
        }
//        passageway.setCashType(config.getCashType());
//        if (request.getPayApiId() != null) {
//            passageway.setPayApiId(request.getPayApiId());
//        }
//        if (request.getPayMethodId() != null) {
//            passageway.setPayMethodId(request.getPayMethodId());
//        }
        if (request.getMinAmount() != null) {
            passageway.setMinAmount(request.getMinAmount());
        }
        if (request.getMaxAmount() != null) {
            passageway.setMaxAmount(request.getMaxAmount());
        }
        if (!StringUtils.isEmpty(request.getLogo())) {
            passageway.setLogo(request.getLogo());
        }
//        if (!StringUtils.isEmpty(request.getCountry())) {
//            passageway.setCountry(request.getCountry());
//        }
//        if (!StringUtils.isBlank(request.getCountry()) && request.getCountry().indexOf("ALL") >= 0) {
//            passageway.setCountry("ALL");
//        }
//        passageway.setNeedKyc(request.getNeedKyc() != null ? request.getNeedKyc() : false);
//        if (!CollectionUtils.isEmpty(request.getExchangeRateList())) {
//            checkExchangeRateList(request.getExchangeRateList());
//            passageway.setExchangeRateJson(JacksonUtil.encode(request.getExchangeRateList()));
//        }
//        if (!CollectionUtils.isEmpty(request.getLanguageList())) {
//            passageway.setLanguageJson(JacksonUtil.encode(request.getLanguageList()));
//        }
        passagewayDao.updateById(passageway);
        PaymentPassagewayDTO result = modelMapper.map(passageway, PaymentPassagewayDTO.class);
        result.setExchangeRateList(request.getExchangeRateList());
        return result;
    }

    /**
     * 删除通道信息
     */
    public void delete(Long id) {
        PaymentPassageway passageway = passagewayDao.selectById(id);
        if (passageway == null) {
            throw new ServerException(2006);
        }
        passagewayDao.deleteById(id);
    }

    /**
     * 根据id查询通道信息
     */
    public PaymentPassagewayDTO query(Long id) {
        PaymentPassageway passageway = passagewayDao.selectById(id);
        if (passageway == null) {
            throw new ServerException(2006);
        }
        PaymentPassagewayDTO result = modelMapper.map(passageway, PaymentPassagewayDTO.class);
        setCompositeList(result, passageway);
        return result;
    }

    /**
     * 根据id查询通道信息
     */
    public PaymentPassagewayDTO get(Long id) {
        PaymentPassageway passageway = passagewayDao.selectById(id);
        if (passageway == null) {
            return null;
        }
        PaymentPassagewayDTO result = modelMapper.map(passageway, PaymentPassagewayDTO.class);
        setCompositeList(result, passageway);
        return result;
    }

    private void setCompositeList(PaymentPassagewayDTO dto, PaymentPassageway entity) {
        // 设置汇率列表
        if (!StringUtils.isBlank(entity.getExchangeRateJson())) {
            dto.setExchangeRateList(
                    JacksonUtil.decode(entity.getExchangeRateJson(), ArrayList.class, ExchangeRateDTO.class));
        } else {
            dto.setExchangeRateList(new ArrayList<>());
        }
        // 设置多语言列表
        if (!StringUtils.isBlank(entity.getLanguageJson())) {
            dto.setLanguageList(
                    JacksonUtil.decode(entity.getLanguageJson(), ArrayList.class, PaymentPassagewayLanguageDTO.class));
        } else {
            dto.setLanguageList(new ArrayList<>());
        }
    }

    private List<PaymentPassagewayDTO> converter(List<PaymentPassageway> list) {
        List<PaymentPassagewayDTO> result = new ArrayList<>();
        if (list != null && list.size() > 0) {
            // 查询支付通道配置
            List<PaymentApiConfig> configList = apiConfigDao.selectList(null);
            for (PaymentPassageway way : list) {
                PaymentPassagewayDTO dto = modelMapper.map(way, PaymentPassagewayDTO.class);
                // 设置支付api名称
                Long payId = dto.getPayApiId();
                List<PaymentMehtod> methodList = null;
                for (PaymentApiConfig config : configList) {
                    if (payId != null && payId.equals(config.getId())) {
                        dto.setPayApiName(config.getName());
                        methodList = config.getMethodList();
                        if (methodList == null && config.getMethodJson() != null
                                && !"".equals(config.getMethodJson())) {
                            try {
                                methodList = objectMapper.readValue(config.getMethodJson(),
                                        new TypeReference<List<PaymentMehtod>>() {
                                        });
                            } catch (Exception e) {
                            }
                            config.setMethodList(methodList);
                        }
                    }
                }
                // 设置支付方式名称
                Long payMethodId = dto.getPayMethodId();
                if (methodList != null) {
                    for (PaymentMehtod method : methodList) {
                        if (payMethodId != null && payMethodId.equals(method.getId())) {
                            dto.setPayMethodName(method.getName());
                        }
                    }
                }
                setCompositeList(dto, way);
                result.add(dto);
            }
        }
        return result;
    }

    public Boolean updateEnable(PaymentPassagewayUpdateEnableRequest request) {
        PaymentPassageway passageway = passagewayDao.selectById(request.getId());
        if (passageway == null) {
            throw new ServerException(2006);
        }
        if (request.getEnable() == null) {
            throw new ServerException(2005);
        }
        passageway.setEnable(request.getEnable());
        passagewayDao.updateById(passageway);
        return true;
    }

//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public Boolean topping(Long id) {
        PaymentPassageway passageway = passagewayDao.selectById(id);
        if (passageway == null) {
            throw new ServerException(1001);
        }
        // 修改当前排序为1
        passageway.setSort(1);
        passagewayDao.updateById(passageway);
        // 修改其他的排序各+1
        QueryWrapper<PaymentPassageway> query = new QueryWrapper<>();
        List<PaymentPassageway> list = passagewayDao.selectList(query);
        for (PaymentPassageway way : list) {
            if (!way.getId().equals(id)) {
                if (way.getSort() != null && way.getSort() >= 1) {
                    way.setSort(way.getSort() + 1);
                } else {
                    way.setSort(999);
                }
                passagewayDao.updateById(way);
            }
        }
        return true;
    }

    public void updateSort(SortRequest request) {
        PaymentPassageway passageway = passagewayDao.selectById(request.getId());
        if (passageway == null) {
            throw new ServerException(2006);
        }
        passageway.setSort(request.getSort());
        passagewayDao.updateById(passageway);
    }


    public List<PaymentPassagewayDTO> queryDisplayList(PaymentCashType cashTypes) {
        QueryWrapper<PaymentPassageway> query = new QueryWrapper<>();
        query.eq(PaymentPassageway.CASH_TYPE, cashTypes);
        query.eq(PaymentPassageway.ENABLE, true);
        query.orderByAsc(PaymentPassageway.SORT).orderByDesc(PaymentPassageway.GMT_CREATE);
        List<PaymentPassageway> list = passagewayDao.selectList(query);
        List<PaymentPassagewayDTO> result = new ArrayList<>();
        if (list != null && list.size() > 0) {
            String locale = LocaleContext.get();
            for (PaymentPassageway way : list) {
                PaymentPassagewayDTO dto = modelMapper.map(way, PaymentPassagewayDTO.class);
                setCompositeList(dto, way);
                dto.setDisplayName(dto.getLanguageDisplayName(locale));
                dto.setDescription(dto.getLanguageDescription(locale));
                result.add(dto);
            }
        }
        return result;
    }

}
