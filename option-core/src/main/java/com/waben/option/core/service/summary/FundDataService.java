package com.waben.option.core.service.summary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.account.UserAccountStatementDTO;
import com.waben.option.common.model.dto.summary.FundDataDTO;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.core.service.statement.AccountStatementService;
import com.waben.option.data.entity.summary.FundData;
import com.waben.option.data.entity.user.AccountStatement;
import com.waben.option.data.repository.order.OrderDao;
import com.waben.option.data.repository.payment.PaymentOrderDao;
import com.waben.option.data.repository.payment.WithdrawOrderDao;
import com.waben.option.data.repository.summary.FundDataDao;
import com.waben.option.data.repository.user.UserDao;
import com.waben.option.data.repository.user.UserMissionCompleteDao;

@Service
public class FundDataService {

	@Resource
	private FundDataDao fundDataDao;

	@Resource
	private IdWorker idWorker;

	@Resource
	private PaymentOrderDao paymentOrderDao;

	@Resource
	private AccountStatementService accountStatementService;

	@Resource
	private UserMissionCompleteDao userMissionCompleteDao;

	@Resource
	private WithdrawOrderDao withdrawOrderDao;

	@Resource
	private OrderDao orderDao;

	@Resource
	private UserDao userDao;

	@Resource
	private ModelMapper modelMapper;

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public PageInfo<FundDataDTO> queryList(String startTime, String endTime, int page, int size) {
		QueryWrapper<FundData> query = new QueryWrapper<>();
		if (startTime != null) {
			query = query.ge(FundData.DAY, startTime);
		}
		if (startTime != null) {
			query = query.le(FundData.DAY, endTime);
		}
		query.orderByDesc(FundData.GMT_CREATE);
		PageInfo<FundDataDTO> pageInfo = new PageInfo<>();
		IPage<FundData> fundDataPage = fundDataDao.selectPage(new Page<>(page, size), query);
		FundData queryFundData = buildFundData(LocalDate.now().format(formatter));
		if (fundDataPage.getRecords().isEmpty()) {
			fundDataPage.setRecords(new ArrayList<>());
		}
		fundDataPage.getRecords().add(0, queryFundData);
		if (!CollectionUtils.isEmpty(fundDataPage.getRecords())) {
			pageInfo.setRecords(fundDataPage.getRecords().stream()
					.map(fundData -> modelMapper.map(fundData, FundDataDTO.class)).collect(Collectors.toList()));
			pageInfo.setTotal(fundDataPage.getTotal() + 1);
			pageInfo.setPage((int) fundDataPage.getPages());
			pageInfo.setSize((int) fundDataPage.getSize());
		}
		return pageInfo;
	}

	public FundData buildFundData(String localDate) {
		FundData fundData = new FundData();
//		List<FundDataDTO> missionCompleteList = userMissionCompleteDao.fundDataStatisticsByMissionComplete(localDate);
//		if (!CollectionUtils.isEmpty(missionCompleteList)) {
//			FundDataDTO mission = missionCompleteList.get(0);
//			modelMapper.map(fundData, mission);
//		}
		// 注册统计
		Integer registerNumber = userDao.registerNumber(localDate);
		fundData.setRegisterNumber(registerNumber != null ? registerNumber : 0);
		Integer inviteRegister = userDao.inviteRegister(localDate);
		fundData.setInviteRegister(inviteRegister != null ? inviteRegister : 0);
		Integer inviteRealRegister = userDao.inviteRealRegister(localDate);
		fundData.setInviteRealRegister(inviteRealRegister != null ? inviteRealRegister : 0);
		// 支付统计
		FundDataDTO allPayment = paymentOrderDao.staPayment(localDate.toString());
		if (allPayment != null) {
			fundData.setAllTotalPayAmount(allPayment.getTotalPayAmount());
			fundData.setAllTotalPayCount(allPayment.getTotalPayCount());
		}
		Integer allPaymentPeopleCount = paymentOrderDao.staPaymentPeopleCount(localDate.toString());
		fundData.setAllTotalPayPeopleCount(allPaymentPeopleCount);
		FundDataDTO notHiddenPayment = paymentOrderDao.staPaymentNotHidden(localDate.toString());
		if (notHiddenPayment != null) {
			fundData.setTotalPayAmount(notHiddenPayment.getTotalPayAmount());
			fundData.setTotalPayCount(notHiddenPayment.getTotalPayCount());
		}
		Integer notHiddenPaymentPeopleCount = paymentOrderDao.staPaymentPeopleCountNotHidden(localDate.toString());
		fundData.setTotalPayPeopleCount(notHiddenPaymentPeopleCount);
		// 提现统计
		FundDataDTO allWithdraw = paymentOrderDao.staWithdraw(localDate.toString());
		if (allPayment != null) {
			fundData.setTotalWithdrawAmount(allWithdraw.getTotalWithdrawAmount());
			fundData.setWithdrawCount(allWithdraw.getWithdrawCount());
		}
		Integer allWithdrawPeopleCount = paymentOrderDao.staWithdrawPeopleCount(localDate.toString());
		fundData.setWithdrawPeopleCount(allWithdrawPeopleCount);

//		List<FundDataDTO> orderList = orderDao.fundDataStatisticsByOrder(localDate);
//		if (!CollectionUtils.isEmpty(orderList)) {
//			FundDataDTO order = orderList.get(0);
//			fundData.setFreeEquipmentIncome(order.getFreeEquipmentIncome());
//			fundData.setAssetsIncome(order.getAssetsIncome());
//		}
//		
//		fundData.setCommissionIncome(getCommissionIncome(localDate));
//		fundData.setInvitationReward(getInvitationReward(localDate));

		fundData.setId(idWorker.nextId());
		fundData.setDay(localDate.toString());
		return fundData;
	}

	public void create(String day) {
		if (day == null) {
			fundDataDao.insert(buildFundData(LocalDate.now().plusDays(-1).format(formatter)));
		} else {
			fundDataDao.insert(buildFundData(day));
		}
	}

	public BigDecimal getCommissionIncome(LocalDate localDate) {
		BigDecimal amount = BigDecimal.ZERO;
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq(AccountStatement.TYPE, TransactionEnum.CREDIT_SUBORDINATE.name());
		queryWrapper.likeRight(AccountStatement.GMT_CREATE, localDate);
		for (int i = 1; i <= 20; i++) {
			amount = amount.add(statementAmountTotal(accountStatementService.selectList(queryWrapper, i)));
		}
		return amount;
	}

	private BigDecimal statementAmountTotal(List<UserAccountStatementDTO> statementList) {
		BigDecimal amount = BigDecimal.ZERO;
		if (!CollectionUtils.isEmpty(statementList)) {
			for (UserAccountStatementDTO accountStatement : statementList) {
				amount = amount.add(accountStatement.getAmount());
			}
		}
		return amount;
	}

	public BigDecimal getInvitationReward(LocalDate localDate) {
		BigDecimal amount = BigDecimal.ZERO;
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.in(AccountStatement.TYPE, TransactionEnum.CREDIT_INVITE_REGISTER,
				TransactionEnum.CREDIT_INVITE_WAGER);
		queryWrapper.likeRight(AccountStatement.GMT_CREATE, localDate);
		for (int i = 1; i <= 20; i++) {
			amount = amount.add(statementAmountTotal(accountStatementService.selectList(queryWrapper, i)));
		}
		return amount;
	}
}