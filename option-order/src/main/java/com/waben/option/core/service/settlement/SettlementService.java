package com.waben.option.core.service.settlement;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.amqp.AMQPMessage;
import com.waben.option.common.amqp.message.OrderGroupSettlementMessage;
import com.waben.option.common.amqp.message.OrderGroupSettlementMessage.OrderSettlementInfo;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.model.dto.resource.LevelIncomeDTO;
import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.common.model.enums.OrderStatusEnum;
import com.waben.option.common.service.AMQPService;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.data.entity.order.Order;
import com.waben.option.data.entity.resource.Commodity;
import com.waben.option.data.entity.resource.Config;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.order.OrderDao;
import com.waben.option.data.repository.resource.CommodityDao;
import com.waben.option.data.repository.resource.ConfigDao;
import com.waben.option.data.repository.user.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SettlementService {

    @Value("${order.settlement.queue.count:10}")
    private Integer queueCount;

    @Resource
    private OrderDao orderDao;

    @Resource
    private UserDao userDao;

    @Resource
    private ConfigDao configDao;

    @Resource
    private CommodityDao commodityDao;

    @Resource
    private AMQPService amqpService;

    public void settlement() {
        log.info("settlement start...");
        Map<Long, User> userMap = userMap();
        Map<Long, List<Order>> orderMap = orderMap();
        log.info("开始处理用户[{}]订单信息", JSON.toJSONString(orderMap));
        for (Map.Entry<Long, List<Order>> entry : orderMap.entrySet()) {
            Long userId = entry.getKey();
            log.info("开始处理用户[{}]订单信息", userId);
            List<Order> orderList = entry.getValue();
            if (!userMap.containsKey(userId)) {
                log.info("用户[{}]不存在", userId);
                return;
            }
            User user = userMap.get(userId);
            // 构建消息
            OrderGroupSettlementMessage message = new OrderGroupSettlementMessage();
            message.setUserId(userId);
            message.setParentId(user.getParentId());
            message.setProfitDivideRatio(profitDivideRatio(1));
            //二级父类
            if (userMap.containsKey(user.getParentId())) {
                User secondUser = userMap.get(user.getParentId());
                message.setSecondParentId(secondUser.getParentId());
                message.setSecondProfitDivideRatio(profitDivideRatio(2));
                //三级父类
                if (userMap.containsKey(secondUser.getParentId())) {
                    User thirdUser = userMap.get(secondUser.getParentId());
                    message.setThirdParentId(thirdUser.getParentId());
                    message.setThirdProfitDivideRatio(profitDivideRatio(3));
                }
            }
            List<OrderSettlementInfo> orderInfoList = new ArrayList<>();
            for (Order order : orderList) {
                try {
                    log.info("处理订单【{}】", JSON.toJSONString(order));
                    OrderSettlementInfo info = new OrderSettlementInfo();
                    info.setId(order.getId());
                    info.setName(order.getName());
                    info.setAmount(order.getAmount());
                    info.setActualAmount(order.getActualAmount());
                    info.setVolume(order.getVolume());
                    info.setReturnRate(order.getReturnRate());
                    info.setProfit(profit(order));
                    if (!order.getFree() && message.getProfitDivideRatio().compareTo(BigDecimal.ZERO) > 0 && message.getParentId() != null
                            && userMap.containsKey(message.getParentId()) && checkSelfTransactionAmount(message.getParentId(),order.getAmount())) {
                        info.setNeedDivide(true);
                        info.setProfitDivide(profitDivide(info.getProfit(), message.getProfitDivideRatio()));
                    } else {
                        info.setNeedDivide(false);
                        info.setProfitDivide(BigDecimal.ZERO);
                    }
                    if (!order.getFree() && null != message.getSecondProfitDivideRatio() && message.getSecondProfitDivideRatio().compareTo(BigDecimal.ZERO) > 0 && message.getSecondParentId() != null
                            && userMap.containsKey(message.getSecondParentId())  && checkSelfTransactionAmount(message.getSecondParentId(),order.getAmount())) {
                        info.setNeedDivide(true);
                        info.setSecondProfitDivide(profitDivide(info.getProfit(), message.getSecondProfitDivideRatio()));
                    } else {
//                    info.setNeedDivide(false);
                        info.setSecondProfitDivide(BigDecimal.ZERO);
                    }
                    if (!order.getFree() && null != message.getThirdProfitDivideRatio() && message.getThirdProfitDivideRatio().compareTo(BigDecimal.ZERO) > 0 && message.getThirdParentId() != null
                            && userMap.containsKey(message.getThirdParentId()) && checkSelfTransactionAmount(message.getThirdParentId(),order.getAmount())) {
                        info.setNeedDivide(true);
                        info.setThirdProfitDivide(profitDivide(info.getProfit(), message.getThirdProfitDivideRatio()));
                    } else {
//                    info.setNeedDivide(false);
                        info.setThirdProfitDivide(BigDecimal.ZERO);
                    }
                    info.setCycle(order.getCycle());
                    info.setWorkedDays(order.getWorkedDays());
                    info.setNeedReturn(order.getNeedReturn());

                    if(info.isNeedDivide()){
                        log.info("settlement 需要分成订单order={}",order);
                    }
                    orderInfoList.add(info);
                } catch (Exception e) {
                    log.error("未知异常", e);
                }
            }
            Integer groupIndex = user.getGroupIndex();
            if (groupIndex == null) {
                groupIndex = 1;
            }
            message.setGroupIndex(groupIndex);
            message.setOrderInfoList(orderInfoList);
            // 发送消息到mq
            int queueIndex = (groupIndex - 1) % queueCount;
            int delayMs = queueIndex * 100000;
            AMQPMessage<OrderGroupSettlementMessage> amqpMessage = new AMQPMessage<OrderGroupSettlementMessage>(
                    message);
            amqpMessage.setMode(AMQPService.AMQPPublishMode.CONFIRMS);
            amqpMessage.setRoutingKey(RabbitMessageQueue.QUEUE_ORDER_GROUP_SETTLEMENT);
            try {
                log.info("发送消息到mq");
                amqpService.convertAndSendDelay(AMQPService.AMQPPublishMode.CONFIRMS,
                        RabbitMessageQueue.getOrderGroupSettlementFanoutDelayExchange(queueIndex),
                        RabbitMessageQueue.QUEUE_ORDER_GROUP_SETTLEMENT, amqpMessage, delayMs);
            } catch (Exception e) {
                log.error("发送订单信息报错", e);
            }
            log.info("settlement end!");
        }

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

    /**
     * 查询订单Map
     * <p>
     * key为用户ID，value为订单列表
     * </p>
     */
    private Map<Long, List<Order>> orderMap() {
        long startTime = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        Map<Long, List<Order>> result = new HashMap<>();
        int page = 1;
        int size = 100000;
        while (true) {
            QueryWrapper<Order> query = new QueryWrapper<>();
            query.select(Order.ID, Order.USER_ID, Order.COMMODITY_ID, Order.NAME, Order.AMOUNT, Order.ACTUAL_AMOUNT,
                    Order.VOLUME,
                    Order.RETURN_RATE, Order.FREE, Order.CYCLE, Order.WORKED_DAYS, Order.NEED_RETURN);
            query.eq(Order.STATUS, OrderStatusEnum.WORKING);
            query.lt(Order.GMT_CREATE, now);
            IPage<Order> pageData = orderDao.selectPage(new Page<>(page, size), query);
            if (pageData == null || pageData.getRecords() == null || pageData.getRecords().size() == 0) {
                break;
            }
            log.info("settlement page query order size: " + pageData.getRecords().size());
            List<Order> list = pageData.getRecords();
            log.info("处理订单[{}]", JSON.toJSONString(list));
            for (Order order : list) {
                try {
                    log.info("开始处理订单[{}]", JSON.toJSONString(order));
                    //计算当前商品是否已过期
                    Commodity commodity = commodityDao.selectById(order.getCommodityId());
                    if (null != commodity) {
                        LocalDateTime expireTime = commodity.getGmtUpdate().plusDays(commodity.getCycle());
                        LocalDateTime nowTime = LocalDateTime.now();
                        if (expireTime.compareTo(nowTime) >= 0 && commodity.getEnable()) {
                            List<Order> orderList = result.get(order.getUserId());
                            if (orderList == null) {
                                orderList = new ArrayList<>();
                                result.put(order.getUserId(), orderList);
                            }
                            orderList.add(order);
                        } else {
                            log.info("修改商品{}为到期", commodity.getId());
                            commodity.setEnable(false);
//                    commodity.setGmtUpdate(LocalDateTime.now());
                            int a = commodityDao.updateById(commodity);
                            if (a > 0) {
                                log.info("修改商品{}为到期成功", commodity.getId());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("处理订单异常", e);
                }
            }
            page++;
        }
        log.info("query order time: " + (System.currentTimeMillis() - startTime));
        return result;
    }

    private Map<Long, List<Order>> orderMapTest() {
        long startTime = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        Map<Long, List<Order>> result = new HashMap<>();
        int page = 1;
        int size = 100000;
        while (true) {
            QueryWrapper<Order> query = new QueryWrapper<>();
            query.select(Order.ID, Order.USER_ID, Order.COMMODITY_ID, Order.NAME, Order.AMOUNT, Order.ACTUAL_AMOUNT,
                    Order.VOLUME,
                    Order.RETURN_RATE, Order.FREE, Order.CYCLE, Order.WORKED_DAYS, Order.NEED_RETURN);
            query.eq(Order.STATUS, OrderStatusEnum.WORKING);
            query.lt(Order.GMT_CREATE, now);
            IPage<Order> pageData = orderDao.selectPage(new Page<>(page, size), query);
            if (pageData == null || pageData.getRecords() == null || pageData.getRecords().size() == 0) {
                break;
            }
            log.info("settlement page query order size: " + pageData.getRecords().size());
            List<Order> list = pageData.getRecords();
            for (Order order : list) {
                //计算当前商品是否已过期
//                Commodity commodity = commodityDao.selectById(order.getCommodityId());
//                LocalDateTime expireTime = commodity.getGmtUpdate().plusDays(commodity.getCycle());
//                LocalDateTime nowTime = LocalDateTime.now();
//                if (expireTime.compareTo(nowTime) >= 0 && commodity.getEnable()) {
                List<Order> orderList = result.get(order.getUserId());
                if (orderList == null) {
                    orderList = new ArrayList<>();
                    result.put(order.getUserId(), orderList);
                }
                orderList.add(order);
//                } else {
//                    log.info("修改商品{}为到期", commodity.getId());
//                    commodity.setEnable(false);
////                    commodity.setGmtUpdate(LocalDateTime.now());
//                    int a = commodityDao.updateById(commodity);
//                    if (a > 0) {
//                        log.info("修改商品{}为到期成功", commodity.getId());
//                    }
//                }
            }
            page++;
        }
        log.info("query order time: " + (System.currentTimeMillis() - startTime));
        return result;
    }

    /**
     * 查询分级收益分成比例
     */
    private BigDecimal profitDivideRatio(int level) {
        QueryWrapper<Config> query = new QueryWrapper<>();
        query.eq(Config.KEY, "levelIncome");
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

    private BigDecimal profit(Order order) {
        return order.getAmount().multiply(order.getVolume()).multiply(order.getReturnRate()).setScale(2,
                RoundingMode.DOWN);
    }

    private BigDecimal profitDivide(BigDecimal profit, BigDecimal profitDivideRatio) {
        return profit.multiply(profitDivideRatio).setScale(2, RoundingMode.DOWN);
    }


    /**
     * 查询自己订单的单个交易额，是否大于等于子级订单的单个交易额。
     * @param userId
     * @param childOrderAmount
     * @return
     */
    public boolean checkSelfTransactionAmount(Long userId,BigDecimal childOrderAmount) {
        QueryWrapper<Order> query = new QueryWrapper<>();
        query.eq(Order.USER_ID,userId);
        query.eq(Order.STATUS, OrderStatusEnum.WORKING);
        query.ge(Order.AMOUNT,childOrderAmount);
        Integer count = orderDao.selectCount(query);
        log.info("checkSelfTransactionAmount 查询父级={}是否大于"+ childOrderAmount + "订单数量为={}",userId,count);
        if(count != null && count > 0){
            return true;
        }
        return false;
    }

}
