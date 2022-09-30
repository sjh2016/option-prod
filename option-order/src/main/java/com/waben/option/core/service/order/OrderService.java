package com.waben.option.core.service.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.amqp.AMQPMessage;
import com.waben.option.common.amqp.message.OrderSettlementMessage;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.component.LocaleContext;
import com.waben.option.common.component.SpringContext;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.constants.DBConstants;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.activity.ActivityAPI;
import com.waben.option.common.interfaces.activity.AllowanceDetailAPI;
import com.waben.option.common.interfaces.resource.ConfigAPI;
import com.waben.option.common.interfaces.resource.MissionActivityAPI;
import com.waben.option.common.interfaces.user.UserMissionAPI;
import com.waben.option.common.message.MessageFactory;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.bean.TradeTransactionRemark;
import com.waben.option.common.model.bean.TransactionRemark;
import com.waben.option.common.model.dto.activity.UpdateJoinDTO;
import com.waben.option.common.model.dto.order.OrderCountDTO;
import com.waben.option.common.model.dto.order.OrderDTO;
import com.waben.option.common.model.dto.order.OrderTotalDTO;
import com.waben.option.common.model.dto.order.OrderUserStaDTO;
import com.waben.option.common.model.dto.resource.ConfigDTO;
import com.waben.option.common.model.dto.resource.LevelIncomeDTO;
import com.waben.option.common.model.dto.resource.OrderCommodityLimitDTO;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.common.model.enums.OrderStatusEnum;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.common.model.query.UserAccountStatementQuery;
import com.waben.option.common.model.request.order.OrderRequest;
import com.waben.option.common.model.request.order.UpdateOrderRequest;
import com.waben.option.common.service.AMQPService;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.common.util.NumberUtil;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.core.service.settlement.SettlementService;
import com.waben.option.data.entity.order.Order;
import com.waben.option.data.entity.order.OrderDynamic;
import com.waben.option.data.entity.resource.Commodity;
import com.waben.option.data.entity.resource.Config;
import com.waben.option.data.entity.user.AccountStatement;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.BaseRepository;
import com.waben.option.data.repository.order.OrderDao;
import com.waben.option.data.repository.order.OrderDynamicDao;
import com.waben.option.data.repository.resource.CommodityDao;
import com.waben.option.data.repository.resource.ConfigDao;
import com.waben.option.data.repository.user.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.waben.option.common.constants.Constants.*;
import static com.waben.option.common.model.enums.TransactionEnum.*;

@Slf4j
@Service
public class OrderService {

    @Value("${order.settlement.queue.count:10}")
    private Integer queueCount;

    @Resource
    private IdWorker idWorker;

    @Resource
    private OrderDao orderDao;

    @Resource
    private OrderDynamicDao orderDynamicDao;

    @Resource
    private ConfigDao configDao;

    @Resource
    private AccountService accountService;

    @Resource
    private AllowanceDetailAPI allowanceDetailAPI;

    @Resource
    private UserDao userDao;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ConfigAPI configAPI;

    @Resource
    private UserMissionAPI userMissionAPI;

    @Resource
    private MissionActivityAPI missionActivityAPI;

    @Resource
    private ActivityAPI activityAPI;

    @Resource
    private CommodityDao commodityDao;

    @Resource
    private StaticConfig staticConfig;

    @Resource
    private AMQPService amqpService;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private RedisTemplate<Serializable, Object> redisTemplate;

    @Resource
    private SettlementService settlementService;

    //    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public String place(OrderRequest request) {
        request.setVolume(BigDecimal.ONE);
        String key = RedisKey.getKey(RedisKey.OPTION_ORDER_COUNT_KEY, LocalDate.now(), request.getCommodityId());
        Integer count = getPlaceCommodityCount(request, key);
        User user = userDao.selectById(request.getUserId());
        Order order = buildOrder(request, user);
        order.setStatus(OrderStatusEnum.WAITING);
        orderDao.insert(order);

        List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
        transactionBeanList.add(AccountTransactionBean.builder().userId(order.getUserId())
                .type(TransactionEnum.DEBIT_WAGER).amount(order.getActualAmount().multiply(order.getVolume()))
                .transactionId(order.getId()).currency(staticConfig.getDefaultCurrency()).build());
        accountService.transaction(order.getUserId(), transactionBeanList);

        UpdateJoinDTO updateJoinDTO = new UpdateJoinDTO();
        updateJoinDTO.setUserId(order.getUserId());
        updateJoinDTO.setJoinUserId(order.getUserId());
        updateJoinDTO.setType(ActivityTypeEnum.INVESTMENT);
        updateJoinDTO.setQuantity(order.getAmount());
        updateJoinDTO.setOrderId(order.getId());
        activityAPI.updateJoin(updateJoinDTO);

        allowanceDetailAPI.distribute(order.getId(), order.getUserId(), order.getCycle(), order.getReturnRate(),
                order.getAmount(), order.getType());

        // 更新产品已购数量
        Commodity commodity = commodityDao.selectById(request.getCommodityId());
        commodity.setUsedQuantity(commodity.getUsedQuantity() + 1);
        commodity.setProductUsedQuantity(commodity.getProductUsedQuantity() + 1);
        commodityDao.updateById(commodity);

        // 赠送产品
        if (!StringUtils.isBlank(commodity.getGiveCommodityId())) {
            placeGive(order.getUserId(), commodity.getGiveCommodityId());
        }

        if (count != null) {
            redisTemplate.opsForValue().set(key, --count);
            redisTemplate.expire(key, 2, TimeUnit.DAYS);
        }

        // 生成订单动态
        OrderDynamic dynamic = new OrderDynamic();
        dynamic.setId(order.getId());
        dynamic.setUserId(order.getUserId());
        dynamic.setUid(user.getUid());
        dynamic.setCommodityId(order.getCommodityId());
        dynamic.setCycle(order.getCycle());
        dynamic.setName(order.getName());
        dynamic.setReturnRate(order.getReturnRate());
        dynamic.setAmount(order.getAmount());
        orderDynamicDao.insert(dynamic);

        try {
//            saveTransInfo(order.getUserId(), user.getGroupIndex(),
//                    order.getActualAmount().multiply(order.getVolume()), order.getId());

//            OrderGroupWagerMessage orderGroupWagerMessage = new OrderGroupWagerMessage();
//            orderGroupWagerMessage.setUserId(order.getUserId());
//            orderGroupWagerMessage.setGroupIndex(user.getGroupIndex());
//            orderGroupWagerMessage.setOrderId(order.getId());
//            orderGroupWagerMessage.setActualAmount(order.getActualAmount().multiply(order.getVolume()));
//            // 发送消息到mq
//            int queueIndex = (user.getGroupIndex() - 1) % queueCount;
//            int delayMs = queueIndex * 100000;
//            AMQPMessage<OrderGroupWagerMessage> amqpMessage = new AMQPMessage<>(
//                    orderGroupWagerMessage);
//            amqpMessage.setMode(AMQPService.AMQPPublishMode.CONFIRMS);
//            amqpMessage.setRoutingKey(RabbitMessageQueue.QUEUE_ORDER_GROUP_WAGER);
//            log.info("发送投资分级消息到mq");
//            amqpService.convertAndSendDelay(AMQPService.AMQPPublishMode.CONFIRMS,
//                    RabbitMessageQueue.getOrderGroupWagerFanoutDelayExchange(queueIndex),
//                    RabbitMessageQueue.QUEUE_ORDER_GROUP_WAGER, amqpMessage, delayMs);
            log.info("发送投资分级消息到成功");
        } catch (Exception e) {
            log.error("发送投资分级信息报错", e);
        }
        return "ok";
    }

    private void saveTransInfo(Long userId, Integer groupIndex, BigDecimal actualAmount, Long orderId) {
        log.info("OrderGroupWagerMessage: userId {}, groupIndex {}", userId, groupIndex);
        List<AccountTransactionBean> firstTransactionBeanList = new ArrayList<>();
        List<AccountTransactionBean> secondParentTransactionBeanList = new ArrayList<>();
        List<AccountTransactionBean> thirdParentTransactionBeanList = new ArrayList<>();
        User user = userDao.selectById(userId);
        User firstUser = null;
        if (null != user) {
            firstUser = userDao.selectById(user.getParentId());
            if (null != firstUser) {
                firstTransactionBeanList.add(AccountTransactionBean.builder().userId(firstUser.getId())
                        .type(TransactionEnum.CREDIT_WAGER).amount(profitDivide(actualAmount,
                                profitDivideRatio(1))).transactionId(orderId)
                        .currency(staticConfig.getDefaultCurrency()).build());
            }
            if (firstTransactionBeanList.size() > 0) {
                accountService.transactionComm(firstUser.getId(), firstTransactionBeanList, 1);
            }
        }
        User secondUser = null;
        if (null != firstUser) {
            secondUser = userDao.selectById(firstUser.getParentId());
            if (null != secondUser) {
                secondParentTransactionBeanList.add(AccountTransactionBean.builder().userId(secondUser.getId())
                        .type(TransactionEnum.CREDIT_WAGER).amount(profitDivide(actualAmount,
                                profitDivideRatio(2))).transactionId(orderId)
                        .currency(staticConfig.getDefaultCurrency()).build());
            }
            if (secondParentTransactionBeanList.size() > 0) {
                accountService.transactionComm(secondUser.getId(), secondParentTransactionBeanList, 2);
            }

        }
        if (null != secondUser) {
            User thirdUser = userDao.selectById(secondUser.getParentId());
            if (null != thirdUser) {
                thirdParentTransactionBeanList.add(AccountTransactionBean.builder().userId(thirdUser.getId())
                        .type(TransactionEnum.CREDIT_WAGER).amount(profitDivide(actualAmount,
                                profitDivideRatio(3))).transactionId(orderId)
                        .currency(staticConfig.getDefaultCurrency()).build());
            }

            if (thirdParentTransactionBeanList.size() > 0) {
                accountService.transactionComm(thirdUser.getId(), thirdParentTransactionBeanList, 3);
            }
        }


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

    private void placeGive(Long userId, String giveCommodityId) {
        OrderRequest request = new OrderRequest();
        request.setUserId(userId);
        request.setCommodityId(Long.parseLong(giveCommodityId.trim()));
        request.setVolume(BigDecimal.ONE);
        request.setGiftLogo(true);

        User user = userDao.selectById(request.getUserId());
        Order order = buildOrder(request, user);
        order.setStatus(OrderStatusEnum.WAITING);
        orderDao.insert(order);

        allowanceDetailAPI.distribute(order.getId(), order.getUserId(), order.getCycle(), order.getReturnRate(),
                order.getAmount(), order.getType());
    }

    private List<OrderCommodityLimitDTO> getOrderCommodityLimitListConfig() {
        try {
            ConfigDTO orderCommodity = configAPI.queryConfig(DBConstants.CONFIG_ORDER_COMMODITY_COUNT_LIMIT_KEY);
            if (orderCommodity != null) {
                return objectMapper.readValue(orderCommodity.getValue(),
                        new TypeReference<List<OrderCommodityLimitDTO>>() {
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer getPlaceCommodityCount(OrderRequest request, String key) {
        List<OrderCommodityLimitDTO> limitDTOList = getOrderCommodityLimitListConfig();
        if (!CollectionUtils.isEmpty(limitDTOList)) {
            for (OrderCommodityLimitDTO limit : limitDTOList) {
                if (request.getCommodityId().equals(limit.getCommodityId())) {
                    Integer count = (Integer) redisTemplate.opsForValue().get(key);
                    if (count == null)
                        count = limit.getLimitCount();
                    if (count <= 0) {
                        throw new ServerException(5012);
                    }
                    return count;
                }
            }
        }
        return null;
    }

    @Transactional
    public void receiveGiveOrder(Long userId) {
        Integer giveOrderCount = orderDao.giveOrderCount(userId);
        if (giveOrderCount != null && giveOrderCount.intValue() > 0) {
            throw new ServerException(5026);
        }
        OrderRequest request = new OrderRequest();
        request.setCommodityId(1L);
        request.setGiftLogo(true);
        request.setVolume(BigDecimal.ONE);
        request.setUserId(userId);
        placeRegister(request);
    }

    //    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public void placeRegister(OrderRequest request) {
        User user = userDao.selectById(request.getUserId());
        Order order = buildOrder(request, user);
        order.setStatus(OrderStatusEnum.WORKING);
        order.setAuditTime(LocalDateTime.now());
        orderDao.insert(order);

        allowanceDetailAPI.distribute(order.getId(), order.getUserId(), order.getCycle(), order.getReturnRate(),
                order.getAmount(), order.getType());

        UpdateJoinDTO updateJoinDTO = new UpdateJoinDTO();
        updateJoinDTO.setUserId(order.getUserId());
        updateJoinDTO.setJoinUserId(order.getUserId());
        updateJoinDTO.setType(ActivityTypeEnum.INVESTMENT);
        updateJoinDTO.setQuantity(order.getAmount());
        updateJoinDTO.setOrderId(order.getId());
        activityAPI.updateJoin(updateJoinDTO);
    }

    private Order buildOrder(OrderRequest request, User user) {
        Commodity commodity = verifyCommodity(request);
        Order order = new Order();
        order.setId(idWorker.nextId());
        order.setRandomId(NumberUtil.generateCode(8));
        order.setUserId(request.getUserId());
        order.setVolume(request.getVolume());
        order.setCommodityId(commodity.getId());
        order.setAmount(commodity.getOriginalPrice());
        order.setActualAmount(commodity.getActualPrice());
        order.setName(commodity.getName());
        order.setSpecification(commodity.getSpecification());
        order.setReturnRate(commodity.getReturnRate());
        order.setIncome(commodity.getIncome());
        order.setImgUrl(commodity.getImgUrl());
        order.setMobilePhone(user.getMobilePhone());
        order.setPanelVolume(commodity.getPanelVolume());
        order.setFree(request.getGiftLogo() != null);
        order.setType(commodity.getType());
        order.setCycle(commodity.getCycle());
        order.setProfit(BigDecimal.ZERO);
        order.setPerProfit(
                commodity.getOriginalPrice().multiply(commodity.getReturnRate()).setScale(0, RoundingMode.DOWN));
        order.setWorkedDays(0);
//        order.setNeedReturn(commodity.getNeedReturn());
        //设置不退回本金
        order.setNeedReturn(false);
        return order;
    }

    /*
     * private String payOrder(OrderRequest request, Order order) { try {
     * PayFrontRequest req = new PayFrontRequest();
     * req.setPassagewayId(request.getPassagewayId() == null ? 1407344680428896256L
     * : request.getPassagewayId()); req.setReqCurrency(request.getReqCurrency() ==
     * null ? CurrencyEnum.IDR : request.getReqCurrency());
     * req.setReqMoney(order.getAmount().multiply(order.getVolume())); Map<String,
     * Object> map = paymentAPI.pay(order.getUserId(), "127.0.0.1", req); return
     * (String) map.get("content"); } catch (Exception e) {
     * log.error("error order place pay |{}|{}", order.getId(), e.getMessage());
     * return null; } }
     */

    private Commodity verifyCommodity(OrderRequest request) {
        Commodity commodity = commodityDao.selectById(request.getCommodityId());
        if (commodity == null) {
            throw new ServerException(1056);
        }
        if (!commodity.getEnable()) {
            throw new ServerException(1057);
        }
        if (commodity.getSoldOut()) {
            throw new ServerException(5011);
        }
        //去掉商品为一的判断
//        if (request.getGiftLogo() == null && request.getCommodityId().compareTo(1L) == 0) {
//            throw new ServerException(5004);
//        }
        if (request.getGiftLogo() == null && request.getCommodityId().compareTo(0L) == 0) {
            throw new ServerException(5004);
        }
        if (commodity.getTotalQuantity().intValue() > 0
                && commodity.getUsedQuantity().intValue() >= commodity.getTotalQuantity().intValue()) {
            throw new ServerException(5012);
        }
        if (commodity.getProductTotalQuantity().intValue() > 0
                && commodity.getProductUsedQuantity().intValue() >= commodity.getProductTotalQuantity().intValue()) {
            throw new ServerException(5025);
        }
//		commodity.setName(getName(commodity.getCode()));
        return commodity;
    }

    private String getName(Integer code) {
        String locale = LocaleContext.getLocale();
        String countryCode = LocaleContext.getCountryCode();
        return MessageFactory.INSTANCE.getMessage(code + "", locale, countryCode);
    }

    private String buildTransactionRemark(Order order) {
        TransactionRemark<TradeTransactionRemark> remark = new TransactionRemark<>(TransactionRemark.RemarkEnum.TRADE,
                TradeTransactionRemark.builder().args(String.valueOf(order.getAmount())).build());
        return remark.toString();
    }

    //    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public void auditOrder(UpdateOrderRequest request) {
        log.info("orderService auditOrder 开始 UpdateOrderRequest={}",request);
        Order order = orderDao.selectById(request.getId());
        if (order == null)
            throw new ServerException(1019);
        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
            // 审核通过
//            if (request.getStatus() == OrderStatusEnum.WORKING) {
//                // 给上级投资分成
//                BigDecimal investmentDivideRatio = queryInvestmentDivideRatio();
//                User user = userDao.selectById(order.getUserId());
//                if (user != null && !user.getParentId().equals(0L)
//                        && investmentDivideRatio.compareTo(BigDecimal.ZERO) > 0 && settlementService.checkSelfTransactionAmount(user.getParentId(),order.getAmount())) {
//                    BigDecimal investmentDivide = computeInvestmentDivide(order.getActualAmount(),
//                            investmentDivideRatio);
//                    List<AccountTransactionBean> accountBeanList = new ArrayList<>();
//                    accountBeanList.add(AccountTransactionBean.builder().userId(user.getParentId())
//                            .type(TransactionEnum.CREDIT_INVITE_WAGER).amount(investmentDivide)
//                            .transactionId(order.getId()).currency(staticConfig.getDefaultCurrency()).build());
//                    log.info("orderService auditOrder transaction 开始");
//                    accountService.transaction(user.getParentId(), accountBeanList);
//                }
//            }
            order.setAuditTime(LocalDateTime.now());
            orderDao.updateById(order);
        }
    }

    private BigDecimal queryInvestmentDivideRatio() {
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
                        if (dto.getLevel() == 1 && dto.getInvestment().compareTo(BigDecimal.ZERO) >= 0) {
                            return dto.getInvestment();
                        }
                    }
                }
            } catch (Exception ex) {
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal computeInvestmentDivide(BigDecimal amount, BigDecimal investmentDivideRatio) {
        return amount.multiply(investmentDivideRatio).setScale(0, RoundingMode.HALF_UP);
    }

    public OrderTotalDTO queryOrderTotalByUserId(Long userId) {
        return orderDao.orderProfit(userId);
    }

    public PageInfo<OrderDTO> queryPage(Long userId, OrderStatusEnum status, int page, int size, String topId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        if (userId != null) {
            queryWrapper = queryWrapper.eq(Order.USER_ID, userId);
        }
        if (status != null) {
            queryWrapper = queryWrapper.eq(Order.STATUS, status);
        }

        if (StringUtils.isNotBlank(topId)){
            QueryWrapper<User> queryUser = new QueryWrapper<>();
            queryUser.eq(User.TOP_ID,topId);
            List<User> users = userDao.selectList(queryUser);
            if (!CollectionUtils.isEmpty(users)){
                List<Long> collect = users.stream().map(User::getId).collect(Collectors.toList());
                queryWrapper.in(Order.USER_ID, collect);
            }
        }
        queryWrapper = queryWrapper.orderByDesc(Order.GMT_CREATE);
        IPage<Order> orderIPage = orderDao.selectPage(new Page<>(page, size), queryWrapper);
        PageInfo<OrderDTO> pageInfo = new PageInfo<>();
        if (orderIPage.getTotal() > 0) {
            pageInfo.setRecords(orderIPage.getRecords().stream().map(order -> modelMapper.map(order, OrderDTO.class))
                    .collect(Collectors.toList()));
            pageInfo.setTotal(orderIPage.getTotal());
            pageInfo.setPage(page);
            pageInfo.setSize(size);
        }
        return pageInfo;
    }

    public void orderSettlement(Integer count) {
        List<Order> orderList = orderDao
                .selectList(new QueryWrapper<Order>().eq(Order.STATUS, OrderStatusEnum.WORKING));
        if (!CollectionUtils.isEmpty(orderList)) {
            Map<Integer, Integer> msMap = getDelayMs();
            for (int i = 0; i < orderList.size(); i++) {
                int num = i % count;
                sendAmq(orderList.get(i).getId(), num, msMap.get(num));
            }
        }
    }

    private void sendAmq(Long orderId, int num, int ms) {
        amqpService.convertAndSendDelay(AMQPService.AMQPPublishMode.CONFIRMS,
                RabbitMessageQueue.getExchangeFanoutSettlementDelay(num), RabbitMessageQueue.QUEUE_ORDER_SETTLEMENT,
                new AMQPMessage<OrderSettlementMessage>(new OrderSettlementMessage(orderId)), ms);
    }

    private Map<Integer, Integer> getDelayMs() {
        int ms = 100000;
        Map<Integer, Integer> baseMap = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            baseMap.put(i, ms * i);
        }
        return baseMap;
    }

    private Map<Long, Map<String, Object>> buildOrderMap() {
        Map<Long, Map<String, Object>> baseMap = new HashMap<>();
        List<Order> orderList = orderDao.selectList(
                new QueryWrapper<Order>().eq(Order.STATUS, OrderStatusEnum.WORKING).orderByAsc(Order.GMT_CREATE));
        for (Order order : orderList) {
            BigDecimal initAmount = BigDecimal.ZERO;
            StringBuffer orderIds = new StringBuffer();
            if (baseMap.containsKey(order.getUserId())) {
                initAmount = (BigDecimal) baseMap.get(order.getUserId()).get("amount");
                orderIds.append(baseMap.get(order.getUserId()).get("orderIds")).append("-");
            }
            Map<String, Object> childMap = new HashMap<>();
            childMap.put("amount", initAmount.add(order.getIncome()));
            childMap.put("orderIds", orderIds.append(order.getId()).toString());
            baseMap.put(order.getUserId(), childMap);
        }
        return baseMap;
    }

    public List<OrderCountDTO> queryOrderCount() {
        List<OrderCommodityLimitDTO> limitDTOList = getOrderCommodityLimitListConfig();
        if (!CollectionUtils.isEmpty(limitDTOList)) {
            List<OrderCountDTO> orderCountDTOList = new ArrayList<>();
            for (OrderCommodityLimitDTO commodity : limitDTOList) {
                String key = RedisKey.getKey(RedisKey.OPTION_ORDER_COUNT_KEY, LocalDate.now(),
                        commodity.getCommodityId());
                Integer count = (Integer) redisTemplate.opsForValue().get(key);
                OrderCountDTO countDTO = new OrderCountDTO();
                countDTO.setCommodityId(commodity.getCommodityId());
                countDTO.setOrderCount(count == null ? commodity.getLimitCount() : count);
                orderCountDTOList.add(countDTO);
            }
            return orderCountDTOList;
        }
        return null;
    }

    public BigDecimal userPlaceCount(Long userId) {
        BigDecimal amount = orderDao.userPlaceCount(userId);
        return amount != null ? amount : BigDecimal.ZERO;
    }

    public OrderUserStaDTO userStaByCache(Long userId) throws ExecutionException {
//        return loadingCache.get(userId);
        return orderDao.queryUserSta(userId);
    }

    public OrderUserStaDTO userSta(Long userId) {
        long timeMillis = System.currentTimeMillis();
        OrderUserStaDTO result = new OrderUserStaDTO();

        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq(Order.USER_ID, userId);
        List<Order> list = orderDao.selectList(wrapper);

        BigDecimal sumAmount = BigDecimal.ZERO;
        BigDecimal sumProfit = BigDecimal.ZERO;
        BigDecimal perProfit = BigDecimal.ZERO;
        Boolean hasGiveOrder = false;
        if (list != null && list.size() > 0) {
            for (Order order : list) {
                if (order.getCommodityId().longValue() == 1L) {
                    hasGiveOrder = true;
                } else {
                    sumAmount = sumAmount.add(order.getActualAmount());
                }
                sumProfit = sumProfit.add(order.getProfit());
                if (order.getStatus() == OrderStatusEnum.WORKING) {
                    perProfit = perProfit.add(order.getPerProfit());
                }
            }
        }
        result.setSumAmount(sumAmount);
        result.setSumProfit(sumProfit);
        result.setPerProfit(perProfit);
        result.setHasGiveOrder(hasGiveOrder);
        log.info("查询用户统计耗时:{}ms", System.currentTimeMillis() - timeMillis);
        return result;
    }

    public OrderUserStaDTO get(User user, int level) {
        OrderUserStaDTO orderUserStaDTO = new OrderUserStaDTO();
        orderUserStaDTO.setSumAmount(BigDecimal.ZERO);
        orderUserStaDTO.setSumProfit(BigDecimal.ZERO);
        orderUserStaDTO.setPerProfit(BigDecimal.ZERO);
        orderUserStaDTO.setHasGiveOrder(false);
        if (level > LIMIT_USER_LEVEL) {
            return orderUserStaDTO;
        }
        OrderUserStaDTO userStaDTO = queryOrder(user.getId());
        if (isTeamUser(user)) {
            orderUserStaDTO.setSumAmount(userStaDTO.getSumAmount().add(getByTeam(userStaDTO.getSumAmount(),
                    level)));
            orderUserStaDTO.setSumProfit(userStaDTO.getSumProfit().add(getByTeam(userStaDTO.getSumProfit(),
                    level)));
            getByTeam(userStaDTO.getPerProfit(), level);
        } else {
            orderUserStaDTO.setSumAmount(userStaDTO.getSumAmount().add(getByUser(userStaDTO.getSumAmount(),
                    level)));
            orderUserStaDTO.setSumProfit(userStaDTO.getSumProfit().add(getByUser(userStaDTO.getSumProfit(),
                    level)));
            orderUserStaDTO.setPerProfit(userStaDTO.getPerProfit().add(getByUser(userStaDTO.getPerProfit(),
                    level)));
        }
        orderUserStaDTO.setHasGiveOrder(userStaDTO.getHasGiveOrder());
        return orderUserStaDTO;
    }

    public boolean isTeamUser(User user) {
        UserAccountStatementQuery query = new UserAccountStatementQuery();
        BaseRepository repo = (BaseRepository) SpringContext.getBean("accountStatementDao" + user.getGroupIndex());
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(AccountStatement.USER_ID, user.getId());
        queryWrapper.in(AccountStatement.TYPE, CREDIT_INVITE_REGISTER, CREDIT_INVITE_WAGER, CREDIT_SUBORDINATE);
        queryWrapper.orderByDesc(AccountStatement.GMT_CREATE);
        queryWrapper.orderByDesc(AccountStatement.ID);
        List<Object> objects = repo.selectList(queryWrapper);
        if (null != objects && !objects.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取团队分销用户收益
     */
    public BigDecimal getByTeam(BigDecimal bigDecimal, int level) {
        if (1 == level) {
            bigDecimal = bigDecimal.multiply(new BigDecimal(TEAM_LEVEL_1_EARNINGS));
        }
        if (2 == level) {
            bigDecimal = bigDecimal.multiply(new BigDecimal(TEAM_LEVEL_2_EARNINGS));
        }
        if (3 == level) {
            bigDecimal = bigDecimal.multiply(new BigDecimal(TEAM_LEVEL_3_EARNINGS));
        }
        return bigDecimal;
    }

    /**
     * 获取用户分销用户收益
     */
    public BigDecimal getByUser(BigDecimal bigDecimal, int level) {
        if (1 == level) {
            bigDecimal = bigDecimal.multiply(new BigDecimal(USER_LEVEL_1_EARNINGS));
        }
        if (2 == level) {
            bigDecimal = bigDecimal.multiply(new BigDecimal(USER_LEVEL_2_EARNINGS));
        }
        if (3 == level) {
            bigDecimal = bigDecimal.multiply(new BigDecimal(USER_LEVEL_3_EARNINGS));
        }
        return bigDecimal;
    }

    public OrderUserStaDTO selectUserByLevel(Long userId, int level) {
        OrderUserStaDTO orderUserStaDTO = new OrderUserStaDTO();
        orderUserStaDTO.setSumAmount(BigDecimal.ZERO);
        orderUserStaDTO.setSumProfit(BigDecimal.ZERO);
        orderUserStaDTO.setPerProfit(BigDecimal.ZERO);
        orderUserStaDTO.setHasGiveOrder(false);
        if (!(level > LIMIT_USER_LEVEL)) {
            List<User> children = userDao.selectList(new QueryWrapper<User>().eq(User.PARENT_ID, userId));
            if (null != children && !children.isEmpty()) {
                for (User child : children) {
                    OrderUserStaDTO userStaDTO = get(child, level);
                    orderUserStaDTO.setSumAmount(orderUserStaDTO.getSumAmount().add(userStaDTO.getSumAmount()));
                    orderUserStaDTO.setSumProfit(orderUserStaDTO.getSumProfit().add(userStaDTO.getSumProfit()));
                    orderUserStaDTO.setPerProfit(orderUserStaDTO.getPerProfit().add(orderUserStaDTO.getPerProfit()));
                    OrderUserStaDTO childStaDto = selectUserByLevel(child.getId(), level + 1);
                    orderUserStaDTO.setSumAmount(orderUserStaDTO.getSumAmount().add(childStaDto.getSumAmount()));
                    orderUserStaDTO.setSumProfit(orderUserStaDTO.getSumProfit().add(childStaDto.getSumProfit()));
                    orderUserStaDTO.setPerProfit(orderUserStaDTO.getPerProfit().add(childStaDto.getPerProfit()));
                }
            }
        }
        return orderUserStaDTO;
    }

    public OrderUserStaDTO queryOrder(Long userId) {
        OrderUserStaDTO result = new OrderUserStaDTO();

        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq(Order.USER_ID, userId);
        List<Order> list = orderDao.selectList(wrapper);

        BigDecimal sumAmount = BigDecimal.ZERO;
        BigDecimal sumProfit = BigDecimal.ZERO;
        BigDecimal perProfit = BigDecimal.ZERO;
        Boolean hasGiveOrder = false;
        if (list != null && list.size() > 0) {
            for (Order order : list) {
                if (order.getCommodityId().longValue() == 1L) {
                    hasGiveOrder = true;
                } else {
                    sumAmount = sumAmount.add(order.getActualAmount());
                }
                sumProfit = sumProfit.add(order.getProfit());
                if (order.getStatus() == OrderStatusEnum.WORKING) {
                    perProfit = perProfit.add(order.getPerProfit());
                }
            }
        }
        result.setSumAmount(sumAmount);
        result.setSumProfit(sumProfit);
        result.setPerProfit(perProfit);
        result.setHasGiveOrder(hasGiveOrder);
        return result;
    }

}
