package com.waben.option.common.constants;

import java.text.MessageFormat;

public class RedisKey {

    public final static String SPLIT = ":";

    public final static String OPTION_KEY = "option" + SPLIT;

    public final static String OPTION_SYSTEM_KEY = OPTION_KEY + "system" + SPLIT;

    public final static String OPTION_AMPQ_RETRY_COUNT_KEY = OPTION_KEY + "amqp" + SPLIT;

    public final static String OPTION_USER_KEY = OPTION_KEY + "user" + SPLIT;
    
    public final static String OPTION_USER_IP_REGISTER_KEY = OPTION_USER_KEY + "ip_register" + SPLIT;
    
    public final static String OPTION_USER_GROUP_KEY = OPTION_USER_KEY + "group";

    public final static String OPTION_USER_PAYMENT_PLATFORM_KEY = OPTION_USER_KEY + "payment" + SPLIT + "platform";

    public final static String OPTION_SYSTEM_VERIFY_CODE = OPTION_SYSTEM_KEY + "verify" + SPLIT + "{0}";
    public final static String OPTION_SYSTEM_VERIFY_EMAIL_COUNT = OPTION_SYSTEM_KEY + "verify" + SPLIT + "email" + SPLIT + "count" + SPLIT + "{0}";

    public final static String OPTION_RESOURCE_KEY = OPTION_KEY + "resource";

    public final static String OPTION_RESOURCE_IMG_CODE_KEY = OPTION_RESOURCE_KEY + "img_code" + SPLIT;

    public final static String OPTION_USER_MAX_SYMBOL_KEY = OPTION_USER_KEY + "max" + SPLIT + "symbol" + SPLIT + "{0}";

    public final static String OPTION_USER_ACCOUNT_KEY = OPTION_KEY + "user" + SPLIT + "account" + SPLIT + "{0}";

    public final static String OPTION_ORDER_COUNT_KEY = OPTION_KEY + "order" + SPLIT + "count" + SPLIT + "{0}" + SPLIT + "{1}";

    public static String getKey(String key, Object... args) {
        return MessageFormat.format(key, args);
    }
}
