package com.waben.option.core.amqp.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.amqp.BaseAMPQConsumer;
import com.waben.option.common.amqp.message.OrderGroupWagerMessage;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.model.dto.resource.LevelIncomeDTO;
import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.data.entity.resource.Config;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.order.OrderDao;
import com.waben.option.data.repository.resource.ConfigDao;
import com.waben.option.data.repository.user.UserDao;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 增加投资收益信息
 */
@Slf4j
//@Component
//@RabbitListener(containerFactory = "rabbitListenerContainerFactory", admin = "amqpAdmin", queues = RabbitMessageQueue.QUEUE_ORDER_GROUP_WAGER)
public class OrderGroupWagerConsumer extends BaseAMPQConsumer<OrderGroupWagerMessage> {

    @Resource
    private OrderDao orderDao;

    @Resource
    private UserDao userDao;

    @Resource
    private AccountService accountService;

    @Resource
    private StaticConfig staticConfig;

    @Resource
    private ConfigDao configDao;

    @Override
    public void handle(OrderGroupWagerMessage message) {
//        try {
//            log.info("OrderGroupWagerMessage: userId {}, groupIndex {}", message.getUserId(), message.getGroupIndex());
//            List<AccountTransactionBean> firstTransactionBeanList = new ArrayList<>();
//            List<AccountTransactionBean> secondParentTransactionBeanList = new ArrayList<>();
//            List<AccountTransactionBean> thirdParentTransactionBeanList = new ArrayList<>();
//            Map<Long, User> userMap = userMap();
//
//            User user = userMap.get(message.getUserId());
//            User firstUser = userMap.get(user.getParentId());
//            if (null != firstUser) {
//                firstTransactionBeanList.add(AccountTransactionBean.builder().userId(firstUser.getId())
//                        .type(TransactionEnum.CREDIT_WAGER).amount(profitDivide(message.getActualAmount(),
//                                profitDivideRatio(1))).transactionId(message.getOrderId())
//                        .currency(staticConfig.getDefaultCurrency()).build());
//            }
//
//            User secondUser = userMap.get(firstUser.getParentId());
//            if (null != secondUser) {
//                secondParentTransactionBeanList.add(AccountTransactionBean.builder().userId(secondUser.getId())
//                        .type(TransactionEnum.CREDIT_WAGER).amount(profitDivide(message.getActualAmount(),
//                                profitDivideRatio(2))).transactionId(message.getOrderId())
//                        .currency(staticConfig.getDefaultCurrency()).build());
//            }
//
//            User thirdUser = userMap.get(secondUser.getParentId());
//            if (null != thirdUser) {
//                thirdParentTransactionBeanList.add(AccountTransactionBean.builder().userId(thirdUser.getId())
//                        .type(TransactionEnum.CREDIT_WAGER).amount(profitDivide(message.getActualAmount(),
//                                profitDivideRatio(3))).transactionId(message.getOrderId())
//                        .currency(staticConfig.getDefaultCurrency()).build());
//            }
//
//            if (firstTransactionBeanList.size() > 0) {
//                accountService.transactionComm(firstUser.getId(), firstTransactionBeanList, 1);
//            }
//            if (secondParentTransactionBeanList.size() > 0) {
//                accountService.transactionComm(secondUser.getId(), secondParentTransactionBeanList, 2);
//            }
//            if (thirdParentTransactionBeanList.size() > 0) {
//                accountService.transactionComm(thirdUser.getId(), thirdParentTransactionBeanList, 3);
//            }
//        } catch (Exception e) {
//            log.error("刷新购买收益数据异常", e);
//        }
    }

    /**
     * 查询分级收益分成比例
     */
    private BigDecimal profitDivideRatio(int level) {
        QueryWrapper<Config> query = new QueryWrapper<>();
        query.eq(Config.KEY, "wager.config");
        Config config = configDao.selectOne(query);
        if (config != null && config.getValue() != null && config.getValue().trim().startsWith("[")
                && config.getValue().trim().endsWith("]")) {
            try {
                List<LevelIncomeDTO> list = JacksonUtil.decode(config.getValue(), ArrayList.class,
                        LevelIncomeDTO.class);
                if (list != null && list.size() > 0) {
                    for (LevelIncomeDTO dto : list) {
                        if (dto.getLevel() == level && dto.getIncome().compareTo(BigDecimal.ZERO) >= 0) {
                            return dto.getIncome();
                        }
                    }
                }
            } catch (Exception ex) {
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal profitDivide(BigDecimal profit, BigDecimal profitDivideRatio) {
        return profit.multiply(profitDivideRatio).setScale(2, RoundingMode.DOWN);
    }

    /**
     * 查询用户Map
     * <p>
     * key为用户ID，value为用户
     * </p>
     */
    private Map<Long, User> userMap() {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.select(User.ID, User.PARENT_ID, User.GROUP_INDEX);
        query.eq(User.AUTHORITY_TYPE, AuthorityEnum.CLIENT);
        List<User> list = userDao.selectList(query);
        Map<Long, User> result = new HashMap<>();
        for (User user : list) {
            result.put(user.getId(), user);
        }
        return result;
    }

}
