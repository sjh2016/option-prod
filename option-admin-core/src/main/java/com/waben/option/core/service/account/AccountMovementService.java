package com.waben.option.core.service.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.dto.account.AccountMovementDTO;
import com.waben.option.common.model.dto.account.UserAccountDTO;
import com.waben.option.common.model.enums.AccountMovementStatusEnum;
import com.waben.option.common.model.enums.CreditDebitEnum;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.common.model.request.user.UserAccountMovementApplyRequest;
import com.waben.option.common.model.request.user.UserAccountMovementAuditRequest;
import com.waben.option.common.model.request.user.UserAccountMovementRequest;
import com.waben.option.data.entity.user.AccountMovement;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.user.AccountMovementDao;
import com.waben.option.data.repository.user.UserDao;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class AccountMovementService {

    @Resource
    private AccountService accountService;

    @Resource
    private AccountMovementDao movementDao;

    @Resource
    private UserDao userDao;
    
	@Resource
	private StaticConfig staticConfig;

    @Resource
    private IdWorker idWorker;

    public PageInfo<AccountMovementDTO> page(UserAccountMovementRequest req) {
        List<AccountMovementDTO> list = movementDao.page(req);
        Long total = movementDao.count(req);
        return new PageInfo<>(list, total, req.getPage(), req.getSize());
    }

//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public void apply(Long applyUserId, UserAccountMovementApplyRequest req) {

        if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServerException(1001);
        }
        if (req.getCreditDebit() != CreditDebitEnum.CREDIT && req.getCreditDebit() != CreditDebitEnum.DEBIT) {
            throw new ServerException(1001);
        }

        User user = userDao.selectById(applyUserId);
        List<UserAccountDTO> account = accountService.queryAccountList(Lists.newArrayList(req.getUserId()), staticConfig.getDefaultCurrency());
        if (CollectionUtils.isEmpty(account)){
            log.info("apply account size:{}",account.size());
            return;
        }
        AccountMovement entity = new AccountMovement();
        entity.setId(idWorker.nextId());
        entity.setAccountId(account.get(0).getId());
        entity.setUserId(req.getUserId());
        entity.setAmount(req.getAmount());
        entity.setCurrency(staticConfig.getDefaultCurrency());
        entity.setCreditDebit(req.getCreditDebit());
        entity.setStatus(AccountMovementStatusEnum.PASSED);
        entity.setApplyUserId(user.getId());
        entity.setApplyUsername(user.getUsername());
        entity.setApplyRemark(req.getApplyRemark());
        movementDao.insert(entity);
        if (req.getCreditDebit() == CreditDebitEnum.CREDIT) {
            // 上分
            List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
            transactionBeanList.add(AccountTransactionBean.builder().userId(entity.getUserId())
                    .type(TransactionEnum.CREDIT_MOVEMENT).amount(entity.getAmount())
                    .transactionId(entity.getId()).currency(entity.getCurrency()).build());
            accountService.transaction(entity.getUserId(), transactionBeanList);
        } else if (entity.getCreditDebit() == CreditDebitEnum.DEBIT) {
            // 下分
            List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
            transactionBeanList.add(AccountTransactionBean.builder().userId(entity.getUserId())
                    .type(TransactionEnum.DEBIT_MOVEMENT).amount(entity.getAmount())
                    .transactionId(entity.getId()).currency(entity.getCurrency()).build());
            accountService.transaction(entity.getUserId(), transactionBeanList);
        }
    }

//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long auditUserId, UserAccountMovementAuditRequest req) {
        AccountMovement entity = movementDao.selectById(req.getId());
        if (entity.getStatus() == AccountMovementStatusEnum.PENDING) {
            User user = userDao.selectById(entity.getUserId());
            if (req.getStatus() == AccountMovementStatusEnum.PASSED) {
                if (entity.getCreditDebit() == CreditDebitEnum.CREDIT) {
                    // 上分
                    List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
                    transactionBeanList.add(AccountTransactionBean.builder().userId(entity.getUserId())
                            .type(TransactionEnum.CREDIT_MOVEMENT).amount(entity.getAmount())
                            .transactionId(entity.getId()).currency(entity.getCurrency()).build());
                    accountService.transaction(entity.getUserId(), transactionBeanList);
                } else if (entity.getCreditDebit() == CreditDebitEnum.DEBIT) {
                    // 下分
                    List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
                    transactionBeanList.add(AccountTransactionBean.builder().userId(entity.getUserId())
                            .type(TransactionEnum.DEBIT_MOVEMENT).amount(entity.getAmount())
                            .transactionId(entity.getId()).currency(entity.getCurrency()).build());
                    accountService.transaction(entity.getUserId(), transactionBeanList);
                }
            }
            entity.setStatus(req.getStatus());
            entity.setAuditRemark(req.getAuditRemark());
            entity.setAuditUserId(user.getId());
            entity.setAuditUsername(user.getUsername());
            entity.setGmtAudit(LocalDateTime.now());
            movementDao.updateById(entity);
        } else {
            throw new ServerException(1001);
        }
    }

}
