package com.waben.option.thirdparty.service.sms.aliyun;

import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.model.enums.EmailTypeEnum;
import com.waben.option.common.util.HtmlUtil;
import com.waben.option.thirdparty.service.sms.AbstractBaseSmsService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractSingleEmailService extends AbstractBaseSmsService {

    private final static String Format = "JSON";
    private final static String SignatureMethod = "HMAC-SHA1";
    private final static String SignatureVersion = "1.0";
    private final static String Version = "2017-06-22"; //"2015-11-23";
    private final static String AddressType = "1";
    private final static String RegionId = "ap-southeast-1";//"cn-hangzhou";
    private final static Boolean ReplyToAddress = Boolean.TRUE;
    private final static HttpMethod method = HttpMethod.POST;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";

    private static final String ENGLISH_REGISTER_HTML_BODY = "email/english_register.html";
    private static final String ENGLISH_CODE_HTML_BODY = "email/english_code.html";
    private static final String ENGLISH_CONTENT_HTML_BODY = "email/english_content.html";
    private static final String ENGLISH_SPECIFIC_CONTENT_HTML_BODY = "email/english_customize_content.html";

    @Value("${email.ali.expiredSeconds:300}")
    private int expiredSeconds;

    @Value("${email.ali.url:https://dm.ap-southeast-1.aliyuncs.com}")
    private String url;

    @Resource
    private OkHttpClient okHttpClient;

    @Resource
    protected RedisTemplate<Serializable, Object> redisTemplate;

    @Override
    public boolean sendCode(String toEmail, String code, EmailTypeEnum type, String content, String ip) {
        verifyRequestCount(ip);
        try {
            TreeMap<String, Object> params = buildTreeMap(toEmail, code, type, content);
            Request postRequest = new Request.Builder().url(url).post(RequestBody
                    .create(MediaType.parse("application/x-www-form-urlencoded"), Objects.requireNonNull(prepareParamStrURLEncoder(params)))).build();
            Response response = okHttpClient.newCall(postRequest).execute();
            if (response.isSuccessful()) {
                String json = response.body().string();
                if (type == EmailTypeEnum.RESET_PASSWORD || type == EmailTypeEnum.VERIFY_CODE) {
                    String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, toEmail);
                    redisTemplate.opsForValue().set(key, code);
                    redisTemplate.expire(key, expiredSeconds, TimeUnit.SECONDS);
                    log.info("SingleEmailService|sendCode|{}|{}|{}", toEmail, code, json);
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("SingleEmailService|{}", e.getMessage());
        }
        return false;
    }

    private TreeMap<String, Object> buildTreeMap(String toEmail, String code, EmailTypeEnum type, String content) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("AccessKeyId", getAccessKey());
        params.put("Action", "SingleSendMail");
        params.put("Format", Format);
        params.put("RegionId", RegionId);
        params.put("SignatureMethod", SignatureMethod);
        params.put("SignatureNonce", UUID.randomUUID().toString());
        params.put("SignatureVersion", SignatureVersion);
        params.put("Timestamp", getUTCTimeStr());
        params.put("Version", Version);
        params.put("AccountName", getAccountName());
        params.put("AddressType", AddressType);
        params.put("HtmlBody", editContext(type, code, content));
        params.put("ReplyToAddress", ReplyToAddress);
        params.put("Subject", getSubject());
        params.put("TagName", getTagName());
        params.put("ToAddress", toEmail);
        params.put("FromAlias", getFromAlias());
        params.put("Signature", getSignature(prepareParamStrURLEncoder(params), method));
        return params;
    }

    private String editContext(EmailTypeEnum type, String code, String content) {
        String message = "";
        switch (type) {
            case REGISTER:
                message = HtmlUtil.readInputStream(ENGLISH_REGISTER_HTML_BODY);
                break;
            case VERIFY_CODE:
            case RESET_PASSWORD:
                message = String.format(HtmlUtil.readInputStream(ENGLISH_CODE_HTML_BODY), code);
                break;
            case CUSTOM_CONTENT:
                message = String.format(HtmlUtil.readInputStream(ENGLISH_CONTENT_HTML_BODY), content);
                break;
            case SPECIFIC_CONTENT:
                message = HtmlUtil.readInputStream(ENGLISH_SPECIFIC_CONTENT_HTML_BODY);
                break;
            default:
                message += "Welcome to MySolarPower!";
                break;
        }
        return message;
    }

    private String prepareParamStrURLEncoder(Map<String, Object> params) {
        try {
            StringBuffer param = new StringBuffer();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (StringUtils.isBlank(entry.getKey()) || null == entry.getValue()) {
                    continue;
                }
                param.append(getUtf8Encoder(entry.getKey()) + "=" + getUtf8Encoder(entry.getValue().toString()) + "&");
            }
            return param.substring(0, param.lastIndexOf("&"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSignature(String param, HttpMethod method) {
        try {
            String toSign = method + "&" + URLEncoder.encode("/", "utf8") + "&" + getUtf8Encoder(param);
            byte[] bytes = HmacSHA1Encrypt(toSign, getSecretKey() + "&");
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String getUtf8Encoder(String param) throws UnsupportedEncodingException {
        return URLEncoder.encode(param, "utf8")
                .replaceAll("\\+", "%20")
                .replaceAll("\\*", "%2A")
                .replaceAll("%7E", "~");
    }

    private static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        Mac mac = Mac.getInstance(MAC_NAME);
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        return mac.doFinal(text);
    }

    private static String getUTCTimeStr() {
        Calendar cal = Calendar.getInstance();
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        String date = dateFormat.format(cal.getTime());
        String[] str = date.split(" ");
        return str[0] + "T" + str[1] + "Z";
    }

    protected abstract String getAccessKey();

    protected abstract String getAccountName();

    protected abstract String getSubject();

    protected abstract String getTagName();

    protected abstract String getSecretKey();

    protected abstract String getFromAlias();
}
