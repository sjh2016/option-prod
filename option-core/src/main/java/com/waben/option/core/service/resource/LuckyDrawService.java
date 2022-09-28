package com.waben.option.core.service.resource;

import static com.waben.option.common.util.LuckyDrawUtil.award;
import static com.waben.option.common.util.LuckyDrawUtil.prizeArr;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.bean.TradeTransactionRemark;
import com.waben.option.common.model.bean.TransactionRemark;
import com.waben.option.common.model.dto.resource.LuckyDrawCommodityDTO;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.data.entity.resource.LuckyDrawCommodity;
import com.waben.option.data.entity.user.Account;
import com.waben.option.data.repository.resource.LuckyDrawCommodityDao;
import com.waben.option.data.repository.user.AccountDao;

@Service
public class LuckyDrawService {

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private AccountDao accountDao;

    @Resource
    private AccountService accountService;

    @Resource
    private LuckyDrawCommodityDao luckyDrawCommodityDao;
    
	@Resource
	private StaticConfig staticConfig;

//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal lucky(Long userId) {
        synchronized (userId) {
            Account account = accountDao.selectOne(new QueryWrapper<Account>().eq(Account.USER_ID, userId).eq(Account.CURRENCY, staticConfig.getDefaultCurrency()));
            if (account == null) throw new ServerException(3001);
            List<AccountTransactionBean> accountBeanList = new ArrayList<>();
            BigDecimal consumeAmount = new BigDecimal(10000);
            if (!account.isBalanceEnough(consumeAmount)) throw new ServerException(3005);
            BigDecimal awardAmount = new BigDecimal(award(prizeArr));
            accountBeanList.add(AccountTransactionBean.builder().userId(userId).type(TransactionEnum.DEBIT_LUCKY_DRAW)
                    .amount(consumeAmount).transactionId(userId).currency(staticConfig.getDefaultCurrency()).time(LocalDateTime.now()).build());
            accountBeanList.add(AccountTransactionBean.builder().userId(userId).type(TransactionEnum.CREDIT_LUCKY_DRAW)
                    .amount(awardAmount).transactionId(userId).currency(staticConfig.getDefaultCurrency()).build());
            accountService.transaction(userId, accountBeanList);
            return awardAmount;
        }
    }

    private String buildTransactionRemark(String args) {
        TransactionRemark<TradeTransactionRemark> remark = new TransactionRemark<>(TransactionRemark.RemarkEnum.TRADE,
                TradeTransactionRemark.builder().args(args).build());
        return remark.toString();
    }


    public List<LuckyDrawCommodityDTO> queryLuckyDrawCommodity() {
        List<LuckyDrawCommodity> commodityList = luckyDrawCommodityDao.selectList(new QueryWrapper<LuckyDrawCommodity>());
        if (!CollectionUtils.isEmpty(commodityList)) {
            return commodityList.stream().map(commodity ->
                    modelMapper.map(commodity, LuckyDrawCommodityDTO.class)).collect(Collectors.toList());
        }
        return null;
    }

}
