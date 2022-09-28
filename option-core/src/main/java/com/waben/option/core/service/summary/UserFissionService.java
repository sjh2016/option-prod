package com.waben.option.core.service.summary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.account.UserAccountStatementDTO;
import com.waben.option.common.model.dto.summary.UserFissionDataDTO;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.core.service.statement.AccountStatementService;
import com.waben.option.data.entity.user.AccountStatement;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.payment.PaymentOrderDao;
import com.waben.option.data.repository.user.UserDao;

@Service
public class UserFissionService {

	@Resource
	private UserDao userDao;

	@Resource
	private AccountStatementService accountStatementService;

	@Resource
	private PaymentOrderDao paymentOrderDao;

	/**
	 * 获取裂变数据
	 *
	 * @return
	 */
	public PageInfo<UserFissionDataDTO> queryList(String mobilePhone, int page, int size) {
		List<UserFissionDataDTO> list = new ArrayList<>();
		PageInfo<UserFissionDataDTO> pageInfo = new PageInfo<>();
		if (page <= 1) {
			page = 1;
		}
		List<UserFissionDataDTO> userFissionDataList = userDao.userFissonList(mobilePhone, page, size);
		Integer count = userDao.userFissonCount(mobilePhone);
		if (userFissionDataList != null && userFissionDataList.size() > 0) {
			for (UserFissionDataDTO userFissionDataDTO : userFissionDataList) {
				// 邀请奖励
				userFissionDataDTO.setInvitationReward(getInvitationReward(userFissionDataDTO.getUserId()));
				// 收益提成
				userFissionDataDTO.setIncome(getTeamIncome(userFissionDataDTO.getUserId()));
				userFissionDataDTO.setDirectPaymentAmount(paymentAmount(userFissionDataDTO.getUserId()));
				list.add(userFissionDataDTO);
			}
		}
		pageInfo.setRecords(userFissionDataList);
		pageInfo.setTotal(count);
		pageInfo.setPage(page);
		pageInfo.setSize(size);
		return pageInfo;
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

	private BigDecimal getInvitationReward(Long userId) {
		BigDecimal amount = BigDecimal.ZERO;
		User user = userDao.selectById(userId);
		if (user != null && user.getGroupIndex() != null) {
			QueryWrapper queryWrapper = new QueryWrapper();
			queryWrapper.in(AccountStatement.TYPE, TransactionEnum.CREDIT_INVITE_REGISTER,
					TransactionEnum.CREDIT_INVITE_WAGER);
			queryWrapper.eq(AccountStatement.USER_ID, userId);
			return statementAmountTotal(accountStatementService.selectList(queryWrapper, user.getGroupIndex()));
		} else {
			return BigDecimal.ZERO;
		}
	}

	private BigDecimal getTeamIncome(Long userId) {
		BigDecimal amount = BigDecimal.ZERO;
		User user = userDao.selectById(userId);
		if (user != null && user.getGroupIndex() != null) {
			QueryWrapper queryWrapper = new QueryWrapper();
			queryWrapper.eq(AccountStatement.TYPE, TransactionEnum.CREDIT_SUBORDINATE);
			queryWrapper.eq(AccountStatement.USER_ID, userId);
			return statementAmountTotal(accountStatementService.selectList(queryWrapper, user.getGroupIndex()));
		} else {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * 下级入金金额
	 *
	 * @param userId
	 * @return
	 */
	public BigDecimal paymentAmount(Long userId) {
		return paymentOrderDao.payAmountTotal(userId);
	}
}
