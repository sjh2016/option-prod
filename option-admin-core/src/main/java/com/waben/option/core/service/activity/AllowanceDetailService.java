package com.waben.option.core.service.activity;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.option.common.component.IdWorker;
import com.waben.option.data.entity.activity.Allowance;
import com.waben.option.data.entity.activity.AllowanceDetail;
import com.waben.option.data.repository.activity.AllowanceDao;
import com.waben.option.data.repository.activity.AllowanceDetailDao;

@RefreshScope
@Service
public class AllowanceDetailService {

	@Resource
	private IdWorker idWorker;

	@Resource
	private AllowanceDao allowanceDao;

	@Resource
	private AllowanceDetailDao allowanceDetailDao;
	
	@Value("${distributeRatio:0.6}")
	private String distributeRatio;

	@Transactional
	public void distribute(Long orderId, Long userId, Integer cycle, BigDecimal returnRate, BigDecimal amount,
			Integer type) {
		// 插入补贴记录
//		BigDecimal distributed = amount.multiply(returnRate).multiply(new BigDecimal(cycle))
//				.multiply(new BigDecimal("0.8"));
		BigDecimal distributed = amount;
		AllowanceDetail detail = new AllowanceDetail();
		detail.setId(idWorker.nextId());
		detail.setOrderId(orderId);
		detail.setUserId(userId);
		detail.setCycle(cycle);
		detail.setReturnRate(returnRate);
		detail.setAmount(amount.multiply(new BigDecimal(distributeRatio)));
		detail.setDistributed(distributed);
		detail.setType(type);
		allowanceDetailDao.insert(detail);
		// 更新总补贴金额
		Allowance allowance = allowanceDao.selectById(type);
		if (allowance != null) {
			allowance.setDistributed(allowance.getDistributed().add(distributed));
			allowanceDao.updateById(allowance);
		}
	}

}
