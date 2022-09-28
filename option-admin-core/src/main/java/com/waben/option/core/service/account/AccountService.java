package com.waben.option.core.service.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.amqp.AMQPMessage;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.dto.account.UserAccountDTO;
import com.waben.option.common.model.dto.account.UserAccountStatementDTO;
import com.waben.option.common.model.dto.summary.UserWithdrawSummaryDTO;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.common.model.query.UserAccountStatementQuery;
import com.waben.option.common.service.AMQPService;
import com.waben.option.core.amqp.message.UserAccountStatementMessage;
import com.waben.option.core.config.RefreshConfig;
import com.waben.option.core.service.statement.AccountStatementService;
import com.waben.option.data.entity.user.Account;
import com.waben.option.data.entity.user.AccountStatement;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.BaseRepository;
import com.waben.option.data.repository.user.AccountDao;
import com.waben.option.data.repository.user.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountService {

	@Resource
	private AccountDao accountDao;

	@Resource
	private AccountStatementService accountStatementService;

	@Resource
	private UserDao userDao;

	@Resource
	private AMQPService amqpService;

	@Resource
	private RefreshConfig refreshConfig;

	@Resource
	private IdWorker idWorker;

	@Resource
	private ModelMapper modelMapper;

	@Resource
	private RedisTemplate<Serializable, Object> redisTemplate;

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void initAccount(Long userId, BigDecimal amount, CurrencyEnum currency, Integer groupIndex) {
		Account account = accountDao
				.selectOne(new QueryWrapper<Account>().eq(Account.USER_ID, userId).eq(Account.CURRENCY, currency));
		if (account == null) {
			account = new Account();
			account.setId(idWorker.nextId());
			account.setUserId(userId);
			account.setFreezeCapital(BigDecimal.ZERO);
			account.setBalance(amount);
			account.setCurrency(currency);
			account.setGroupIndex(groupIndex);
			accountDao.insert(account);
		}
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void statement(Long userId, List<AccountTransactionBean> transactionBeanList) {
		Map<CurrencyEnum, Account> accountMap = buildAccountMap(userId);
		for (AccountTransactionBean transactionBean : transactionBeanList) {
			Long statementId = idWorker.nextId();
			Account account = accountMap.get(transactionBean.getCurrency());
			if (transactionBean.getType() == TransactionEnum.CREDIT_REGISTER_GIFT) {
				account.setBalance(transactionBean.getAmount());
				transactionBean.setTime(LocalDateTime.now());
			}
			saveAccountStatement(userId, statementId, transactionBean, transactionBean.getAmount(), account);
		}
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public synchronized List<Long> transaction(Long userId, List<AccountTransactionBean> transactionBeanList) {
		List<Long> ids = new LinkedList<>();
		User user = userDao.selectById(userId);
		synchronized (userId) {
			Map<CurrencyEnum, Account> accountMap = buildAccountMap(userId);
			for (AccountTransactionBean transactionBean : transactionBeanList) {
				if (!refreshConfig.checkFlowAllow(transactionBean.getType(), user.getIsBlack())) {
					continue;
				}
				BigDecimal amount = transactionBean.getAmount();
				Long statementId = idWorker.nextId();
				ids.add(statementId);
				Account account = accountMap.get(transactionBean.getCurrency());
				switch (transactionBean.getType().getCreditDebitType()) {
				case CREDIT:
					amount = credit(account, transactionBean);
					break;
				case DEBIT:
					amount = debit(account, transactionBean);
					break;
				case FREEZE:
					amount = freeze(account, transactionBean);
					break;
				case UNFREEZE:
					amount = unfreeze(account, transactionBean);
					break;
				}
				accountDao.updateById(account);
				saveAccountStatement(userId, statementId, transactionBean, amount, account);
//				String key = RedisKey.getKey(RedisKey.OPTION_USER_ACCOUNT_KEY, userId);
//				redisTemplate.opsForValue().set(key, account);
			}
		}
		return ids;
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	void saveAccountStatement(Long userId, Long statementId, AccountTransactionBean transactionBean, BigDecimal amount,
			Account account) {
		AccountStatement statement = new AccountStatement();
		statement.setId(statementId);
		statement.setUserId(userId);
		statement.setAccountId(account.getId());
		statement.setAmount(amount);
		statement.setBalance(account.getBalance());
		statement.setType(transactionBean.getType());
		statement.setCreditDebit(transactionBean.getType().getCreditDebitType());
		statement.setCurrency(transactionBean.getCurrency());
		statement.setFreezeCapital(account.getFreezeCapital());
		statement.setRemark(transactionBean.getRemark());
		statement.setTransactionId(transactionBean.getTransactionId());
		statement.setUniqueId(idWorker.nextId());
		statement.setGmtCreate(LocalDateTime.now());
		if (transactionBean.getTime() != null) {
			statement.setGmtCreate(LocalDateTime.now().plusSeconds(-2));
		}
		BaseRepository statementRepo = accountStatementService.getRepo(account.getGroupIndex());
		Object entity = accountStatementService.getEntity(statement, account.getGroupIndex());
		statementRepo.insert(entity);
	}

	private void accountStatementConsumer(Long userId, AccountTransactionBean transactionBean, BigDecimal amount,
			Account account) {
		amqpService.convertAndSend(AMQPService.AMQPPublishMode.CONFIRMS,
				RabbitMessageQueue.QUEUE_USER_ACCOUNT_STATEMENT,
				new AMQPMessage<UserAccountStatementMessage>(new UserAccountStatementMessage(userId, account.getId(),
						transactionBean.getType().getCreditDebitType(), amount, account.getBalance(),
						transactionBean.getType(), transactionBean.getCurrency(), account.getFreezeCapital(),
						transactionBean.getRemark(), transactionBean.getTransactionId(), transactionBean.getTime())));
	}

	private Map<CurrencyEnum, Account> buildAccountMap(Long userId) {
		Map<CurrencyEnum, Account> accountMap = new HashMap<>();
		List<Account> userAccountList = accountDao.selectList(new QueryWrapper<Account>().eq(Account.USER_ID, userId));
		if (!CollectionUtils.isEmpty(userAccountList)) {
			for (Account account : userAccountList) {
				accountMap.put(account.getCurrency(), account);
			}
		}
		return accountMap;
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	BigDecimal credit(Account account, AccountTransactionBean transactionBean) {
		account.credit(transactionBean.getAmount());
		return transactionBean.getAmount();
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	BigDecimal debit(Account account, AccountTransactionBean transactionBean) {
		if (account.isBalanceEnough(transactionBean.getAmount())) {
			account.debit(transactionBean.getAmount());
		} else {
			throw new ServerException(3002);
		}
		return transactionBean.getAmount();
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	BigDecimal freeze(Account account, AccountTransactionBean transactionBean) {
		if (account.isBalanceEnough(transactionBean.getAmount())) {
			account.freeze(transactionBean.getAmount());
			return transactionBean.getAmount();
		} else {
			throw new ServerException(3003);
		}
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	BigDecimal unfreeze(Account account, AccountTransactionBean transactionBean) {
		if (account.isUnfreezeFundEnough(transactionBean.getAmount())) {
			account.unfreeze(transactionBean.getAmount());
			return transactionBean.getAmount();
		} else {
			throw new ServerException(3004);
		}
	}

	private boolean checkTransactionBean(List<AccountTransactionBean> transactionBeanList) {
		for (AccountTransactionBean transactionBean : transactionBeanList) {
			if (transactionBean.getAmount().compareTo(BigDecimal.ZERO) < 0) {
				return false;
			}
		}
		return true;
	}

	public PageInfo<UserAccountStatementDTO> queryAccountStatementPage(Long userId, UserAccountStatementQuery query) {
		PageInfo<UserAccountStatementDTO> pageInfo = new PageInfo<>();
		pageInfo.setPage(query.getPage());
		pageInfo.setSize(query.getSize());
		if (userId == null) {
			return pageInfo;
		}
		User user = userDao.selectById(userId);
		if (user == null || user.getGroupIndex() == null) {
			return pageInfo;
		}
		return accountStatementService.selectPage(user, query);
	}

	public Integer queryStatementCount(Long userId, TransactionEnum type) {
		User user = userDao.selectById(userId);
		if (user != null && user.getGroupIndex() != null) {
			QueryWrapper queryWrapper = new QueryWrapper();
			queryWrapper.eq(AccountStatement.TYPE, type);
			queryWrapper.eq(AccountStatement.USER_ID, userId);
			return accountStatementService.selectCount(queryWrapper, user.getGroupIndex());
		} else {
			return 0;
		}
	}

	/**
	 * 用户多币种资金账户查询
	 *
	 * @param uidList
	 * @return
	 */
	public List<UserAccountDTO> queryAccountList(List<Long> uidList, CurrencyEnum currency) {
		QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
		queryWrapper.select(Account.ID, Account.USER_ID, Account.BALANCE, Account.FREEZE_CAPITAL);
		if (!CollectionUtils.isEmpty(uidList)) {
			queryWrapper = queryWrapper.in(Account.USER_ID, uidList);
		}
		if (currency != null) {
			queryWrapper = queryWrapper.eq(Account.CURRENCY, currency);
		}
		List<Account> userAccountList = accountDao.selectList(queryWrapper);
		return userAccountList.stream().map(userAccount -> modelMapper.map(userAccount, UserAccountDTO.class))
				.collect(Collectors.toList());
	}

	public UserAccountDTO queryAccount(Long userId) {
//		String key = RedisKey.getKey(RedisKey.OPTION_USER_ACCOUNT_KEY, userId);
//		Account account = (Account) redisTemplate.opsForValue().get(key);
//		if (account == null) {
//			account = accountDao.selectOne(new QueryWrapper<Account>().eq(Account.USER_ID, userId));
//			redisTemplate.opsForValue().set(key, account);
//		}
//		return modelMapper.map(account, UserAccountDTO.class);

		Account account = accountDao.selectOne(new QueryWrapper<Account>().eq(Account.USER_ID, userId));
		if (account != null) {
			return modelMapper.map(account, UserAccountDTO.class);
		}
		return null;
	}

	public List<UserWithdrawSummaryDTO> userWithdrawSummaryByUsers(List<Long> uidList) {
//        List<UserWithdrawSummaryDTO> summaryDTOList = new ArrayList<>();
//        List<AccountStatement> statementList = userAccountStatementDao.
//                selectList(new QueryWrapper<AccountStatement>().in(AccountStatement.USER_ID, uidList));
//        if (!CollectionUtils.isEmpty(statementList)) {
//            Map<Long, Map<TransactionEnum, BigDecimal>> baseMap = buildWithdrawMap(uidList);
//            buildMap(statementList, baseMap);
//            setList(summaryDTOList, baseMap);
//        }
//
//        return summaryDTOList;
		return new ArrayList<>();
	}

	private void setList(List<UserWithdrawSummaryDTO> summaryDTOList,
			Map<Long, Map<TransactionEnum, BigDecimal>> baseMap) {
		for (Map.Entry<Long, Map<TransactionEnum, BigDecimal>> entry : baseMap.entrySet()) {
			UserWithdrawSummaryDTO summaryDTO = new UserWithdrawSummaryDTO();
			summaryDTO.setUserId(entry.getKey());
			for (Map.Entry<TransactionEnum, BigDecimal> entryEnum : entry.getValue().entrySet()) {
				BigDecimal amount = entryEnum.getValue();
				if (amount.compareTo(BigDecimal.ZERO) == 0)
					continue;
				switch (entryEnum.getKey()) {
				case CREDIT_INVITE_REGISTER:
					summaryDTO.setTotalInviteAmount(summaryDTO.getTotalInviteAmount().add(amount));
					break;
				case CREDIT_LOGIN_PROFIT:
					summaryDTO.setTotalLoginAmount(summaryDTO.getTotalLoginAmount().add(amount));
					break;
				case CREDIT_PROFIT:
				case CREDIT_INVITE_WAGER:
				case CREDIT_SUBORDINATE:
					summaryDTO.setTotalDivideAmount(summaryDTO.getTotalDivideAmount().add(amount));
					break;
				}
			}
			summaryDTOList.add(summaryDTO);
		}
	}

	private void buildMap(List<AccountStatement> statementList, Map<Long, Map<TransactionEnum, BigDecimal>> baseMap) {
		for (AccountStatement statement : statementList) {
			Map<TransactionEnum, BigDecimal> enumBigDecimalMap = baseMap.get(statement.getUserId());
			for (Map.Entry<TransactionEnum, BigDecimal> entry : enumBigDecimalMap.entrySet()) {
				if (statement.getType() == entry.getKey()) {
					entry.setValue(entry.getValue().add(statement.getAmount()));
					break;
				}
			}
		}
	}

	private Map<Long, Map<TransactionEnum, BigDecimal>> buildWithdrawMap(List<Long> uidList) {
		Map<Long, Map<TransactionEnum, BigDecimal>> baseMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(uidList)) {
			List<TransactionEnum> transactionEnumList = transactionEnumList();
			for (Long aLong : uidList) {
				Map<TransactionEnum, BigDecimal> enumBMap = new HashMap<>();
				for (TransactionEnum transactionEnum : transactionEnumList) {
					enumBMap.put(transactionEnum, BigDecimal.ZERO);
				}
				baseMap.put(aLong, enumBMap);
			}
		}
		return baseMap;
	}

	private List<TransactionEnum> transactionEnumList() {
		List<TransactionEnum> transactionEnums = new ArrayList<>();
		transactionEnums.add(TransactionEnum.CREDIT_INVITE_REGISTER);
		transactionEnums.add(TransactionEnum.CREDIT_LOGIN_PROFIT);
		transactionEnums.add(TransactionEnum.CREDIT_PROFIT);
		transactionEnums.add(TransactionEnum.CREDIT_INVITE_WAGER);
		transactionEnums.add(TransactionEnum.CREDIT_SUBORDINATE);
		return transactionEnums;
	}

}
