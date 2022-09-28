package com.waben.option.core.service.payment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.dto.payment.PaymentFeeConfigDTO;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.FeeMethodType;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.request.payment.PaymentFeeConfigRequest;
import com.waben.option.data.entity.payment.PaymentApiConfig;
import com.waben.option.data.entity.payment.PaymentFeeConfig;
import com.waben.option.data.repository.payment.PaymentApiConfigDao;
import com.waben.option.data.repository.payment.PaymentFeeConfigDao;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PaymentFeeConfigService {

    @Resource
    private PaymentFeeConfigDao feeConfigDao;

    @Resource
    private PaymentApiConfigDao apiConfigDao;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private IdWorker idWorker;

    public PaymentFeeConfigDTO query(PaymentCashType cashType, CurrencyEnum currency, String burseType, Long payApiId) {
        QueryWrapper<PaymentFeeConfig> query = new QueryWrapper<>();
        query.orderByDesc(PaymentFeeConfig.GMT_CREATE);
        query.eq(PaymentFeeConfig.CASH_TYPE, cashType);
        query.eq(PaymentFeeConfig.CURRENCY, currency);
        if (burseType != null) {
            query.eq(PaymentFeeConfig.BURSE_TYPE, burseType);
        } else {
            query.isNull(PaymentFeeConfig.BURSE_TYPE);
        }
        if (payApiId != null) {
            query.eq(PaymentFeeConfig.PAY_API_ID, payApiId);
        }
        List<PaymentFeeConfig> list = feeConfigDao.selectList(query);
        if (list != null && list.size() > 0) {
            return modelMapper.map(list.get(0), PaymentFeeConfigDTO.class);
        } else {
            return null;
        }
    }

    /**
     * 获取出入金手续费配置列表
     */
    public List<PaymentFeeConfigDTO> list() {
        QueryWrapper<PaymentFeeConfig> query = new QueryWrapper<>();
        query.orderByDesc(PaymentFeeConfig.GMT_CREATE);
        List<PaymentFeeConfig> list = feeConfigDao.selectList(query);
        List<PaymentFeeConfigDTO> result = new ArrayList<>();
        if (list != null && list.size() > 0) {
            List<PaymentApiConfig> configList = apiConfigDao.selectList(null);
            for (PaymentFeeConfig feeConfig : list) {
                PaymentFeeConfigDTO dto = modelMapper.map(feeConfig, PaymentFeeConfigDTO.class);
                // 设置支付通道名称
                for (PaymentApiConfig apiConfig : configList) {
                    if (apiConfig.getId() == dto.getPayApiId()) {
                        dto.setPayApiName(apiConfig.getName());
                        break;
                    }
                }
                result.add(dto);
            }
        }
        return result;
    }

    /**
     * 新增出入金手续费配置
     */
    public PaymentFeeConfigDTO create(PaymentFeeConfigRequest request) {
        if (request.getCashType() == null || request.getCurrency() == null || request.getFeeType() == null
                || request.getFee() == null || request.getFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new ServerException(1001);
        }
        PaymentFeeConfig feeConfig = new PaymentFeeConfig();
        if (request.getCashType() == PaymentCashType.PAYMENT_COIN
                || request.getCashType() == PaymentCashType.PAYMENT_OTC) {
            if (request.getPayApiId() == null) {
                throw new ServerException(1001);
            }
            PaymentApiConfig config = apiConfigDao.selectById(request.getPayApiId());
            if (config == null) {
                throw new ServerException(1001);
            }
            // 检查是否已添加
            QueryWrapper<PaymentFeeConfig> query = new QueryWrapper<>();
            query.eq(PaymentFeeConfig.PAY_API_ID, request.getPayApiId());
            List<PaymentFeeConfig> list = feeConfigDao.selectList(query);
            if (list != null && list.size() > 0) {
                throw new ServerException(2007);
            }
            feeConfig.setCashType(config.getCashType());
            feeConfig.setPayApiId(request.getPayApiId());
        } else {
            // 检查是否已添加
            QueryWrapper<PaymentFeeConfig> query = new QueryWrapper<>();
            query.eq(PaymentFeeConfig.CASH_TYPE, request.getCashType());
            if (request.getCashType() == PaymentCashType.WITHDRAW_COIN) {
                if (request.getBurseType() != null) {
                    query.eq(PaymentFeeConfig.BURSE_TYPE, request.getBurseType());
                } else {
                    query.isNull(PaymentFeeConfig.BURSE_TYPE);
                }
            }
            if (request.getCashType() == PaymentCashType.WITHDRAW_OTC) {
                if (request.getPayApiId() == null) {
                    throw new ServerException(1001);
                }
                PaymentApiConfig config = apiConfigDao.selectById(request.getPayApiId());
                if (config == null) {
                    throw new ServerException(1001);
                }
                query.eq(PaymentFeeConfig.PAY_API_ID, request.getPayApiId());
            }
            List<PaymentFeeConfig> checkList = feeConfigDao.selectList(query);
            if (checkList != null && checkList.size() > 0) {
                throw new ServerException(2008);
            }
            feeConfig.setCashType(request.getCashType());
            feeConfig.setPayApiId(request.getCashType() == PaymentCashType.WITHDRAW_OTC ? request.getPayApiId() : null);
        }
        feeConfig.setId(idWorker.nextId());
        feeConfig.setCurrency(request.getCurrency());
        feeConfig.setFeeType(request.getFeeType());
        feeConfig.setFee(request.getFee());
        if (request.getCashType() == PaymentCashType.WITHDRAW_COIN) {
            feeConfig.setBurseType(request.getBurseType());
        }
        feeConfigDao.insert(feeConfig);
        return modelMapper.map(feeConfig, PaymentFeeConfigDTO.class);
    }

    /**
     * 修改出入金手续费配置
     */
    public PaymentFeeConfigDTO update(PaymentFeeConfigRequest request) {
        PaymentFeeConfig feeConfig = feeConfigDao.selectById(request.getId());
        if (feeConfig == null) {
            throw new ServerException(2009);
        }
        if (request.getCashType() == null || request.getCurrency() == null || request.getFeeType() == null
                || request.getFee() == null || request.getFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new ServerException(1001);
        }
        if (request.getCashType() == PaymentCashType.PAYMENT_COIN
                || request.getCashType() == PaymentCashType.PAYMENT_OTC) {
            if (request.getPayApiId() == null) {
                throw new ServerException(1001);
            }
            PaymentApiConfig config = apiConfigDao.selectById(request.getPayApiId());
            if (config == null) {
                throw new ServerException(1001);
            }
            // 检查是否已添加
            QueryWrapper<PaymentFeeConfig> query = new QueryWrapper<>();
            query.eq(PaymentFeeConfig.PAY_API_ID, request.getPayApiId());
            query.ne(PaymentFeeConfig.ID, request.getId());
            List<PaymentFeeConfig> list = feeConfigDao.selectList(query);
            if (list != null && list.size() > 0) {
                throw new ServerException(2007);
            }
            feeConfig.setCashType(config.getCashType());
            feeConfig.setPayApiId(request.getPayApiId());
        } else {
            // 检查是否已添加
            QueryWrapper<PaymentFeeConfig> query = new QueryWrapper<>();
            query.eq(PaymentFeeConfig.CASH_TYPE, request.getCashType());
            if (request.getCashType() == PaymentCashType.WITHDRAW_COIN) {
                if (request.getBurseType() != null) {
                    query.eq(PaymentFeeConfig.BURSE_TYPE, request.getBurseType());
                } else {
                    query.isNull(PaymentFeeConfig.BURSE_TYPE);
                }
            }
            if (request.getCashType() == PaymentCashType.WITHDRAW_OTC) {
                if (request.getPayApiId() == null) {
                    throw new ServerException(1001);
                }
                PaymentApiConfig config = apiConfigDao.selectById(request.getPayApiId());
                if (config == null) {
                    throw new ServerException(1001);
                }
                query.eq(PaymentFeeConfig.PAY_API_ID, request.getPayApiId());
            }
            query.ne(PaymentFeeConfig.ID, request.getId());
            List<PaymentFeeConfig> checkList = feeConfigDao.selectList(query);
            if (checkList != null && checkList.size() > 0) {
                throw new ServerException(2009);
            }
            feeConfig.setCashType(request.getCashType());
            feeConfig.setPayApiId(request.getCashType() == PaymentCashType.WITHDRAW_OTC ? request.getPayApiId() : null);
        }
        feeConfig.setCurrency(request.getCurrency());
        feeConfig.setFeeType(request.getFeeType());
        feeConfig.setFee(request.getFee());
        if (request.getCashType() == PaymentCashType.WITHDRAW_COIN) {
            feeConfig.setBurseType(request.getBurseType());
        }
        feeConfigDao.updateById(feeConfig);
        return modelMapper.map(feeConfig, PaymentFeeConfigDTO.class);
    }

    /**
     * 删除出入金手续费配置
     */
    public void delete(Long id) {
        PaymentFeeConfig passageway = feeConfigDao.selectById(id);
        if (passageway == null) {
            throw new ServerException(2009);
        }
        feeConfigDao.deleteById(id);
    }

    public BigDecimal computeFee(String orderNo, PaymentCashType cashType, CurrencyEnum currency, String burseType,
                                 BigDecimal realNum, Long payApiId) {
        BigDecimal result = BigDecimal.ZERO;
        if (cashType == PaymentCashType.PAYMENT_OTC || cashType == PaymentCashType.PAYMENT_COIN) {
            QueryWrapper<PaymentFeeConfig> query = new QueryWrapper<>();
            query.eq(PaymentFeeConfig.CURRENCY, currency.name());
            query.eq(PaymentFeeConfig.CASH_TYPE, cashType.name());
            query.eq(PaymentFeeConfig.PAY_API_ID, payApiId);
            query.orderByDesc(PaymentFeeConfig.GMT_CREATE);
            List<PaymentFeeConfig> feeList = feeConfigDao.selectList(query);
            if (feeList != null && feeList.size() > 0) {
                result = fee(currency, realNum, feeList.get(0).getFeeType(), feeList.get(0).getFee());
            }
        } else if (cashType == PaymentCashType.WITHDRAW_COIN) {
            QueryWrapper<PaymentFeeConfig> query = new QueryWrapper<>();
            query.eq(PaymentFeeConfig.CURRENCY, currency.name());
            query.eq(PaymentFeeConfig.CASH_TYPE, cashType.name());
            if (burseType != null) {
                query.eq(PaymentFeeConfig.BURSE_TYPE, burseType);
            } else {
                query.isNull(PaymentFeeConfig.BURSE_TYPE);
            }
            query.orderByDesc(PaymentFeeConfig.GMT_CREATE);
            List<PaymentFeeConfig> feeList = feeConfigDao.selectList(query);
            if (feeList != null && feeList.size() > 0) {
                result = fee(currency, realNum, feeList.get(0).getFeeType(), feeList.get(0).getFee());
            }
        } else if (cashType == PaymentCashType.WITHDRAW_OTC) {
            QueryWrapper<PaymentFeeConfig> query = new QueryWrapper<>();
            query.eq(PaymentFeeConfig.CASH_TYPE, cashType.name());
            query.eq(PaymentFeeConfig.PAY_API_ID, payApiId);
            query.orderByDesc(PaymentFeeConfig.GMT_CREATE);
            List<PaymentFeeConfig> feeList = feeConfigDao.selectList(query);
            if (feeList != null && feeList.size() > 0) {
                result = fee(currency, realNum, feeList.get(0).getFeeType(), feeList.get(0).getFee());
            }
        }
        log.info("compute fee order {}, cashType {}, currency {}, realNum {}, payApiId {}, fee {}", orderNo, cashType,
                currency, realNum, payApiId, result);
        return result;
    }

    private BigDecimal fee(CurrencyEnum currency, BigDecimal money, FeeMethodType feeType, BigDecimal fee) {
        BigDecimal result = BigDecimal.ZERO;
        if (feeType == FeeMethodType.FIXED) {
            result = fee.setScale(currency.getPrecision(), RoundingMode.HALF_UP);
        } else if (feeType == FeeMethodType.RATIO) {
            result = money.multiply(fee).divide(new BigDecimal(100), currency.getPrecision(), RoundingMode.HALF_UP);
        }
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            result = BigDecimal.ZERO;
        }
        return result;
    }

}
