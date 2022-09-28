package com.waben.option.core.amqp.user;

import com.waben.option.common.amqp.BaseAMPQConsumer;
import com.waben.option.common.amqp.message.UserRegisterMessage;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.constants.DBConstants;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.interfaces.order.OrderAPI;
import com.waben.option.common.interfaces.point.PointProductOrderAPI;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.dto.resource.ConfigDTO;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.common.model.request.order.OrderRequest;
import com.waben.option.common.model.request.point.PointPlaceOrderRequest;
import com.waben.option.core.config.RefreshConfig;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.core.service.resource.ConfigService;
import com.waben.option.core.service.user.UserService;
import com.waben.option.data.entity.resource.Commodity;
import com.waben.option.data.repository.resource.CommodityDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RabbitListener(containerFactory = "rabbitListenerContainerFactory", admin = "amqpAdmin", queues = RabbitMessageQueue.QUEUE_USER_REGISTER)
public class UserRegisterConsumer extends BaseAMPQConsumer<UserRegisterMessage> {

    @Resource
    private UserService userService;

    @Resource
    private AccountService accountService;

    @Resource
    private OrderAPI orderAPI;

    @Resource
    private ConfigService configService;

    @Resource
    private CommodityDao commodityDao;

    @Resource
    private PointProductOrderAPI pointProductOrderAPI;

    @Resource
    private StaticConfig staticConfig;

    @Resource
    private RefreshConfig refreshConfig;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(UserRegisterMessage message) {
        // 注册赠送项目
        if (staticConfig.isContract()) {
            if (refreshConfig.isAutoGiveOrder()) {
                //去掉赠送商品 2022-04-19
//                Commodity commodity = commodityDao.selectById(1L);
//                if (commodity != null) {
//                    orderAPI.placeRegister(buildOrderRequest(commodity, message.getUserId()));
//                }
            }
        } else {
            PointPlaceOrderRequest req = new PointPlaceOrderRequest();
            req.setProductId(1L);
            pointProductOrderAPI.place(message.getUserId(), true, req);
        }
        //审核后才生产注册奖励
//        registerGift(message.getParentId());
        // 增加邀请人数
        userService.createInviteRegister(message.getUserId(), message.getParentId());
    }

    private void registerGift(Long userId) {
        // 注册赠送资金
        ConfigDTO giftConfig = configService.queryConfig(DBConstants.REGISTERED_GIFT);
        if (giftConfig != null && !StringUtils.isBlank(giftConfig.getValue())) {
            BigDecimal gift = new BigDecimal(giftConfig.getValue().trim());
            if (gift.compareTo(BigDecimal.ZERO) > 0) {
                List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
                transactionBeanList.add(AccountTransactionBean.builder().userId(userId)
                        .type(TransactionEnum.CREDIT_REGISTER_GIFT).amount(gift).transactionId(userId)
                        .currency(staticConfig.getDefaultCurrency()).build());
                accountService.transaction(userId, transactionBeanList);
            }
        }
    }

    private OrderRequest buildOrderRequest(Commodity commodity, Long userId) {
        OrderRequest request = new OrderRequest();
        request.setUserId(userId);
        request.setCommodityId(commodity.getId());
        request.setVolume(BigDecimal.ONE);
        request.setGiftLogo(true);
        return request;
    }
}
