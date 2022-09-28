package com.waben.option.core.service.simulate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.data.entity.order.Order;
import com.waben.option.data.entity.simulate.InvestData;
import com.waben.option.data.entity.simulate.UidData;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.order.OrderDao;
import com.waben.option.data.repository.simulate.InvestDataDao;
import com.waben.option.data.repository.simulate.UidDataDao;
import com.waben.option.data.repository.user.UserDao;

@Service
public class InvestDataService {

	@Resource
	private InvestDataDao investDataDao;

	@Resource
	private UidDataDao uidDataDao;

	@Resource
	private OrderDao orderDao;

	@Resource
	private UserDao userDao;

	@Resource
	private IdWorker idWorker;

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private Random randow = new Random();

	public BigDecimal[] amountArr = new BigDecimal[] { new BigDecimal("100000.00"), new BigDecimal("300000.00"),
			new BigDecimal("500000.00"), new BigDecimal("1000000.00"), new BigDecimal("2000000.00"),
			new BigDecimal("4000000.00"), new BigDecimal("8000000.00"), new BigDecimal("10000000.00") };

	@Transactional
	public void generateData(String day, int number, String amountStr) {
		LocalDateTime date = LocalDateTime.parse(day + " 00:00:00", formatter);
		// 要随机的金额
		BigDecimal[] amountComArr = amountArr;
		if (!StringUtils.isEmpty(amountStr)) {
			String[] amountStrArr = amountStr.split(",");
			amountComArr = new BigDecimal[amountStrArr.length];
			for (int i = 0; i < amountStrArr.length; i++) {
				amountComArr[i] = new BigDecimal(amountStrArr[i]);
			}
		}
		// 查询真实数据
		QueryWrapper<Order> query = new QueryWrapper<>();
		query.ge(Order.GMT_CREATE, date);
		query.lt(Order.GMT_CREATE, date.plusDays(1));
		query.ne(Order.COMMODITY_ID, 1L);
		List<Order> orderList = orderDao.selectList(query);
		for (Order order : orderList) {
			User user = userDao.selectById(order.getUserId());
			insert(order.getUserId(), user != null ? user.getUid() : null, order.getAmount(), order.getGmtCreate());
		}
		// 生成数据
		if (number > orderList.size()) {
			List<UidData> uidList = uidDataDao.selectList(new QueryWrapper<>());
			if (uidList != null && uidList.size() > 0) {
				int num = number - orderList.size();
				for (int i = 0; i < num; i++) {
					int uidIndex = randow.nextInt(uidList.size());
					UidData uidData = uidList.get(uidIndex);
					BigDecimal amount = amountArr[randow.nextInt(amountArr.length)];
					insert(null, uidData.getUid(), amount, date);
					uidData.setInvestAmount(uidData.getInvestAmount().add(amount));
					uidDataDao.updateById(uidData);
				}
			}
		}
	}

	@Transactional
	public void insert(Long userId, String uid, BigDecimal amount, LocalDateTime time) {
		InvestData data = new InvestData();
		data.setId(idWorker.nextId());
		data.setUserId(userId);
		data.setUid(uid);
		data.setAmount(amount);
		data.setTime(time);
		investDataDao.insert(data);
	}

}
