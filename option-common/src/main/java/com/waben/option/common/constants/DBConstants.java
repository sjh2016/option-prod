package com.waben.option.common.constants;

/**
 * @author: Peter
 * @date: 2021/6/23 14:57
 */
public class DBConstants {

    public final static String CONFIG_CURRENCY = "currency";

    // 上传文件路径配置
    public final static String CONFIG_PATH_UPLOAD_KEY = "path.upload";
    // 图片url前缀
    public final static String CONFIG_URL_IMAGE_KEY = "url.image";
    // 充值选项和赠送 配置Key
    public final static String CONFIG_RECHARGE_ITEM_GIVE = "rechargeItemAndGive";

    public final static String CONFIG_WITHDRAW_KEY = "withdraw";

    // 获取马甲包配置的url
    public final static String APP_VEST_URL = "appVestUrl";

    // 站外推送beanName
    public final static String OUTSIDE_PUSH_BEAN_NAME = "outsidePushBeanName";

    // 站外推送消息模板，key-value格式，key @see OutsidePushMessageType，value @see OutsidePushMessageTemplateDTO
    public final static String OUTSIDE_PUSH_MESSAGE_TEMPLATE = "outsidePushMessageTemplate";

    // 站外推送广播消息的马甲包列表 List<UserVestDTO>
    public final static String OUTSIDE_BROADCAST_VEST_LIST = "outsideBroadcastVestList";

    // 上传文件路径配置
    public final static String CONFIG_SEND_EMAIL_BEAN_KEY = "send.email.bean";
    public final static String CONFIG_ORDER_COMMODITY_COUNT_LIMIT_KEY = "order.commodity.count.limit";
    
    // 注册赠送资金
    public final static String REGISTERED_GIFT = "registeredGift";
    // 投资赠送资金
    public final static String INVESTMENT_GIFT = "investmentGift";
    
    // USDT汇率
    public final static String USDT_RATE = "usdtRate";
    
}
