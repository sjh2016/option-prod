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
import com.waben.option.common.model.enums.PaymentOrderStatusEnum;
import com.waben.option.data.entity.payment.PaymentOrder;
import com.waben.option.data.entity.simulate.PaymentData;
import com.waben.option.data.entity.simulate.UidData;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.payment.PaymentOrderDao;
import com.waben.option.data.repository.simulate.PaymentDataDao;
import com.waben.option.data.repository.simulate.UidDataDao;
import com.waben.option.data.repository.user.UserDao;

@Service
public class PaymentDataService {

	@Resource
	private PaymentDataDao paymentDataDao;

	@Resource
	private UidDataDao uidDataDao;

	@Resource
	private PaymentOrderDao paymentOrderDao;

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
		QueryWrapper<PaymentOrder> query = new QueryWrapper<>();
		query.eq(PaymentOrder.STATUS, PaymentOrderStatusEnum.SUCCESS);
		query.ge(PaymentOrder.ARRIVAL_TIME, date);
		query.lt(PaymentOrder.ARRIVAL_TIME, date.plusDays(1));
		List<PaymentOrder> orderList = paymentOrderDao.selectList(query);
		for (PaymentOrder order : orderList) {
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
					uidData.setPaymentAmount(uidData.getPaymentAmount().add(amount));
					uidDataDao.updateById(uidData);
				}
			}
		}
	}

	@Transactional
	public void insert(Long userId, String uid, BigDecimal amount, LocalDateTime time) {
		PaymentData data = new PaymentData();
		data.setId(idWorker.nextId());
		data.setUserId(userId);
		data.setUid(uid);
		data.setAmount(amount);
		data.setTime(time);
		paymentDataDao.insert(data);
	}

}
