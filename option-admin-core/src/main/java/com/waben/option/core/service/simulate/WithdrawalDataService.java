package com.waben.option.core.service.simulate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.model.enums.WithdrawOrderStatusEnum;
import com.waben.option.data.entity.payment.WithdrawOrder;
import com.waben.option.data.entity.simulate.UidData;
import com.waben.option.data.entity.simulate.WithdrawalData;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.payment.WithdrawOrderDao;
import com.waben.option.data.repository.simulate.UidDataDao;
import com.waben.option.data.repository.simulate.WithdrawalDataDao;
import com.waben.option.data.repository.user.UserDao;

@Service
public class WithdrawalDataService {

	@Resource
	private WithdrawalDataDao withdrawalDataDao;

	@Resource
	private UidDataDao uidDataDao;

	@Resource
	private WithdrawOrderDao withdrawOrderDao;

	@Resource
	private UserDao userDao;

	@Resource
	private IdWorker idWorker;

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private Random randow = new Random();

	private BigDecimal minAmount = new BigDecimal("100000");

	private BigDecimal maxAmount = new BigDecimal("3000000");

	@Transactional
	public void generateData(String day, int number, String amountStr) {
		LocalDateTime date = LocalDateTime.parse(day + " 00:00:00", formatter);
		int cha = maxAmount.subtract(minAmount).divide(new BigDecimal(10000)).intValue();
		// 查询真实数据
		QueryWrapper<WithdrawOrder> query = new QueryWrapper<>();
		query.eq(WithdrawOrder.STATUS, WithdrawOrderStatusEnum.SUCCESSFUL);
		query.ge(WithdrawOrder.ARRIVAL_TIME, date);
		query.lt(WithdrawOrder.ARRIVAL_TIME, date.plusDays(1));
		List<WithdrawOrder> orderList = withdrawOrderDao.selectList(query);
		for (WithdrawOrder order : orderList) {
			User user = userDao.selectById(order.getUserId());
			insert(order.getUserId(), user != null ? user.getUid() : null, order.getReqMoney(), order.getArrivalTime());
		}
		// 生成数据
		if (number > orderList.size()) {
			QueryWrapper<UidData> uidQuery = new QueryWrapper<>();
			uidQuery.ge(UidData.INVEST_AMOUNT, "0");
			List<UidData> uidList = uidDataDao.selectList(uidQuery);
			if (uidList != null && uidList.size() > 0) {
				int num = number - orderList.size();
				for (int i = 0; i < num; i++) {
					int uidIndex = randow.nextInt(uidList.size());
					UidData uidData = uidList.get(uidIndex);
					// 要随机的金额
					BigDecimal amount = minAmount.add(new BigDecimal(randow.nextInt(cha) * 10000));
					insert(null, uidData.getUid(), amount, date);
					uidData.setWithdrawalAmount(uidData.getWithdrawalAmount().add(amount));
					uidDataDao.updateById(uidData);
				}
			}
		}
	}

	@Transactional
	public void insert(Long userId, String uid, BigDecimal amount, LocalDateTime time) {
		WithdrawalData data = new WithdrawalData();
		data.setId(idWorker.nextId());
		data.setUserId(userId);
		data.setUid(uid);
		data.setAmount(amount);
		data.setTime(time);
		withdrawalDataDao.insert(data);
	}

}
