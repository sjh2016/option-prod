package com.waben.option.core.service.simulate;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.option.common.component.IdWorker;
import com.waben.option.common.util.NumberUtil;
import com.waben.option.data.entity.simulate.UidData;
import com.waben.option.data.repository.simulate.UidDataDao;

@Service
public class UidDataService {

	@Resource
	private IdWorker idWorker;

	@Resource
	private UidDataDao uidDataDao;

	@Transactional
	public void generate(int number) {
		for (int i = 0; i < number; i++) {
			String uid = NumberUtil.generateCode(8);
			UidData entity = new UidData();
			entity.setId(idWorker.nextId());
			entity.setUid(uid);
			entity.setInvestAmount(BigDecimal.ZERO);
			entity.setWithdrawalAmount(BigDecimal.ZERO);
			uidDataDao.insert(entity);
		}
	}

}
