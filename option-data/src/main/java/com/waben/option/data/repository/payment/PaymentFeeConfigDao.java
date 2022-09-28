package com.waben.option.data.repository.payment;

import com.waben.option.data.entity.payment.PaymentFeeConfig;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentFeeConfigDao extends BaseRepository<PaymentFeeConfig> {

}
