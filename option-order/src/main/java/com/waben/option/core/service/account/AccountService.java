package com.waben.option.core.service.account;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.core.configuration.RefreshConfig;
import com.waben.option.data.entity.user.Account;
import com.waben.option.data.entity.user.AccountStatement;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.BaseRepository;
import com.waben.option.data.repository.user.AccountDao;
import com.waben.option.data.repository.user.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AccountService {

    @Resource
    private IdWorker idWorker;

    @Resource
    private RedisTemplate<Serializable, Object> redisTemplate;

    @Resource
    private UserDao userDao;

    @Resource
    private AccountDao accountDao;

    @Resource
    private AccountStatementService accountStatementService;

    @Resource
    private RefreshConfig refreshConfig;

    public void checkAmount(Long userId, CurrencyEnum currency, BigDecimal amount) {
        QueryWrapper<Account> query = new QueryWrapper<>();
        query.eq(Account.USER_ID, userId);
        query.eq(Account.CURRENCY, currency.name());
        Account account = accountDao.selectOne(query);
        if (account == null || account.getAvailableBalance().compareTo(amount) < 0) {
            throw new ServerException(3002);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized void transaction(Long userId, List<AccountTransactionBean> transactionBeanList) {
        log.info("AccountService transaction 开始 userId={} transactionBeanList={}",userId, JSON.toJSON(transactionBeanList));
        User user = userDao.selectById(userId);
        synchronized (userId) {
            Map<CurrencyEnum, Account> accountMap = buildAccountMap(userId);
            for (AccountTransactionBean transactionBean : transactionBeanList) {
                if (!refreshConfig.checkFlowAllow(transactionBean.getType(), user.getIsBlack())) {
                    continue;
                }
                BigDecimal amount = transactionBean.getAmount();
                Long statementId = idWorker.nextId();
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
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized void transactionComm(Long userId, List<AccountTransactionBean> transactionBeanList, int level) {
        User user = userDao.selectById(userId);
        synchronized (userId) {
            Map<CurrencyEnum, Account> accountMap = buildAccountMap(userId);
            for (AccountTransactionBean transactionBean : transactionBeanList) {
                if (!refreshConfig.checkFlowAllow(transactionBean.getType(), user.getIsBlack())) {
                    continue;
                }
                BigDecimal amount = transactionBean.getAmount();
                Long statementId = idWorker.nextId();
                Account account = accountMap.get(transactionBean.getCurrency());
                switch (transactionBean.getType().getCreditDebitType()) {
                    case CREDIT:
                        amount = creditComm(account, transactionBean, level);
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
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Transactional(rollbackFor = Exception.class)
    private void saveAccountStatement(Long userId, Long statementId, AccountTransactionBean transactionBean,
                                      BigDecimal amount, Account account) {
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

    @Transactional(rollbackFor = Exception.class)
    BigDecimal credit(Account account, AccountTransactionBean transactionBean) {
        try {
            if (null == transactionBean) {
                log.info("保存记录信息为空，返回计算金额0");
                return BigDecimal.ZERO;
            }
            if (null == transactionBean.getAmount()) {
                transactionBean.setAmount(BigDecimal.ZERO);
            }
            account.credit(transactionBean.getAmount());
        } catch (Exception e) {
            log.error("[" + account.getUserId() + "]保存数据异常", e);
        }
        return transactionBean.getAmount();
    }

    @Transactional(rollbackFor = Exception.class)
    BigDecimal creditComm(Account account, AccountTransactionBean transactionBean, int level) {
        try {
            if (null == transactionBean) {
                log.info("保存记录信息为空，返回计算金额0");
                return BigDecimal.ZERO;
            }
            if (null == transactionBean.getAmount()) {
                transactionBean.setAmount(BigDecimal.ZERO);
            }
            account.credit(transactionBean.getAmount());
            if (1 == level) {
                account.commission(transactionBean.getAmount());
            }
            if (2 == level) {
                account.secondCommission(transactionBean.getAmount());
            }
            if (3 == level) {
                account.thridCommission(transactionBean.getAmount());
            }
        } catch (Exception e) {
            log.error("[" + account.getUserId() + "]保存数据异常", e);
        }
        return transactionBean.getAmount();
    }

    @Transactional(rollbackFor = Exception.class)
    BigDecimal debit(Account account, AccountTransactionBean transactionBean) {
        if (account.isBalanceEnough(transactionBean.getAmount())) {
            account.debit(transactionBean.getAmount());
        } else {
            throw new ServerException(3002);
        }
        return transactionBean.getAmount();
    }

    @Transactional(rollbackFor = Exception.class)
    BigDecimal freeze(Account account, AccountTransactionBean transactionBean) {
        if (account.isBalanceEnough(transactionBean.getAmount())) {
            account.freeze(transactionBean.getAmount());
            return transactionBean.getAmount();
        } else {
            throw new ServerException(3003);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    BigDecimal unfreeze(Account account, AccountTransactionBean transactionBean) {
        if (account.isUnfreezeFundEnough(transactionBean.getAmount())) {
            account.unfreeze(transactionBean.getAmount());
            return transactionBean.getAmount();
        } else {
            throw new ServerException(3004);
        }
    }

}
