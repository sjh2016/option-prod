package com.waben.option.thirdparty.amqp;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.waben.option.common.amqp.BaseAMPQConsumer;
import com.waben.option.common.amqp.message.SendEmailMessage;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.model.enums.EmailTypeEnum;
import com.waben.option.common.util.HtmlUtil;
import com.waben.option.common.util.JacksonUtil;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Component
@RabbitListener(containerFactory = "rabbitListenerContainerFactory", admin = "amqpAdmin", queues = RabbitMessageQueue.QUEUE_SEND_EMAIL)
public class SendEmailConsumer extends BaseAMPQConsumer<SendEmailMessage> {

	@Value("${email.subject:Uprofit Security Verification}")
	private String subject;

	@Value("${email.body:[Uprofit] Dear customers, Your verification code is: %s}")
	private String body;

	@Value("${email.expiredSeconds:1800}")
	private int expiredSeconds;
	
	@Value("${email.codeHtml:english_code.html}")
	private String codeHtml;

	/**************************************************************************************/

	@Value("${email.amazon.formName:Uprofit}")
	private String fromName;

	@Value("${email.amazon.from:service@usdtprofit.com}")
	private String from;

	@Value("${email.amazon.smtpUsername:AKIAVCKOH35MWMPCV2KR}")
	private String smtpUsername;

	@Value("${email.amazon.smtpPassword:BEfN5RWgD4o+pLcalyFmKMJThV4yZYeGcRWA78hZhlMg}")
	private String smtpPassword;

	@Value("${email.amazon.host:email-smtp.ap-southeast-1.amazonaws.com}")
	private String host;

	@Value("${email.amazon.port:587}")
	private String port;

	/**************************************************************************************/

	@Value("${email.tencent.url:https://ses.tencentcloudapi.com}")
	private String tencentUrl;

	@Value("${email.tencent.formName:IvansCredict}")
	private String tencentFromName;

	@Value("${email.tencent.from:support@ivanscredict.com}")
	private String tencentFrom;

	@Value("${email.tencent.secretId:AKIDOr9GLCT7BVgcK1j8OExTjxv40afc6bbq}")
	private String tencentSecretId;

	@Value("${email.tencent.secretKey:A4iP4VkjI4bDk12oLde6OTIpHI6f2dz4}")
	private String tencentSecretKey;

	@Value("${email.tencent.templateId:19860}")
	private String tencentTemplateId;

	@Resource
	private RedisTemplate<Serializable, Object> redisTemplate;

	@Resource
	private OkHttpClient okHttpClient;

	private final static Charset UTF8 = StandardCharsets.UTF_8;
	private final static String CT_JSON = "application/json; charset=utf-8";
	private static final String ENGLISH_REGISTER_HTML_BODY = "email/english_register.html";
	private static final String ENGLISH_CODE_HTML_BODY = "email/english_code.html";
	private static final String ENGLISH_CONTENT_HTML_BODY = "email/english_content.html";
	private static final String ENGLISH_SPECIFIC_CONTENT_HTML_BODY = "email/english_customize_content.html";

//    public static void main(String[] args) throws Exception {
//    	Properties props = System.getProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.port", 2525);
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.auth", "true");
//        
//        Session session = Session.getDefaultInstance(props);
//        MimeMessage msg = new MimeMessage(session);
//        msg.setFrom(new InternetAddress("hoonwang841@gmail.com", "IvansCredict"));
//        msg.setRecipient(Message.RecipientType.TO, new InternetAddress("mengqilufei147@gmail.com"));
//        msg.setSubject("IvansCredict");
//        String context = editContext(EmailTypeEnum.VERIFY_CODE, "128567", null);
//        msg.setContent(context, "text/html;charset=UTF-8");
//        Transport transport = session.getTransport();
//        transport.connect("smtp.elasticemail.com", "support@ivanscredict.com", "549A8C3FE1FAFDF4582DA04122B03536A5CC");
//        // Send the email.
//        transport.sendMessage(msg, msg.getAllRecipients());
//        transport.close();
//	}

	@Override
	public void handle(SendEmailMessage message) {
		if (message.getApiType() != null && message.getApiType().intValue() == 2) {
			tencentEmail(message);
		} else {
			amazonEmail(message);
		}
	}

	public void tencentEmail(SendEmailMessage message) {
		log.info("start tencent email send, {}", message.toString());
		if (StringUtils.isBlank(message.getToEmail())) {
			log.info("tencent email send error, email is null");
			return;
		}
		try {
			// 构建body
			String payload = String.format(
					"{\"FromEmailAddress\": \"%s <%s>\",\"Destination\": [\"%s\"],\"Template\": {\"TemplateID\": %s,\"TemplateData\": \"{\\\"verifyCode\\\":\\\"%s\\\"}\"},\"Subject\": \"%s\"}",
					tencentFromName, tencentFrom, message.getToEmail(), tencentTemplateId, message.getCode(), subject);
			// 构建headers
			Map<String, String> headers = tencentHeaders(payload);
			// 请求上游
			log.info("tencent email send param: {}", payload);
			Request.Builder builder = new Request.Builder().url(tencentUrl)
					.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), payload));
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				builder.addHeader(entry.getKey(), entry.getValue());
			}
			Request postRequest = builder.build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			if (response.isSuccessful()) {
				log.info("tencent email send response: " + json);
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				boolean isErorr = jsonNode.get("Response").has("Error");
				if (isErorr) {
					log.error("The tencent email was not sent. Error message: "
							+ jsonNode.get("Response").get("Error").get("Message").asText());
				}
			} else {
				log.error("The tencent email was not sent. Error message: http response is not successful!");
			}
			if (message.getType() == EmailTypeEnum.RESET_PASSWORD || message.getType() == EmailTypeEnum.VERIFY_CODE) {
				String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, message.getToEmail());
				redisTemplate.opsForValue().set(key, message.getCode());
				redisTemplate.expire(key, expiredSeconds, TimeUnit.SECONDS);
				log.info("TencentEmailService|sendCode|{}", message.getCode());
			}
		} catch (Exception ex) {
			log.error("The tencent email was not sent. Error message: " + ex.getMessage());
		}
	}

	private byte[] hmac256(byte[] key, String msg) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
		mac.init(secretKeySpec);
		return mac.doFinal(msg.getBytes(UTF8));
	}

	private String sha256Hex(String s) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] d = md.digest(s.getBytes(UTF8));
		return DatatypeConverter.printHexBinary(d).toLowerCase();
	}

	private TreeMap<String, String> tencentHeaders(String payload) throws Exception {
		String service = "ses";
		String host = "ses.tencentcloudapi.com";
		String region = "ap-hongkong";
		String action = "SendEmail";
		String version = "2020-10-02";
		String algorithm = "TC3-HMAC-SHA256";
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 注意时区，否则容易出错
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String date = sdf.format(new Date(Long.valueOf(timestamp + "000")));

		// ************* 步骤 1：拼接规范请求串 *************
		String httpRequestMethod = "POST";
		String canonicalUri = "/";
		String canonicalQueryString = "";
		String canonicalHeaders = "content-type:application/json; charset=utf-8\n" + "host:" + host + "\n";
		String signedHeaders = "content-type;host";

		String hashedRequestPayload = sha256Hex(payload);
		String canonicalRequest = httpRequestMethod + "\n" + canonicalUri + "\n" + canonicalQueryString + "\n"
				+ canonicalHeaders + "\n" + signedHeaders + "\n" + hashedRequestPayload;

		// ************* 步骤 2：拼接待签名字符串 *************
		String credentialScope = date + "/" + service + "/" + "tc3_request";
		String hashedCanonicalRequest = sha256Hex(canonicalRequest);
		String stringToSign = algorithm + "\n" + timestamp + "\n" + credentialScope + "\n" + hashedCanonicalRequest;

		// ************* 步骤 3：计算签名 *************
		byte[] secretDate = hmac256(("TC3" + tencentSecretKey).getBytes(UTF8), date);
		byte[] secretService = hmac256(secretDate, service);
		byte[] secretSigning = hmac256(secretService, "tc3_request");
		String signature = DatatypeConverter.printHexBinary(hmac256(secretSigning, stringToSign)).toLowerCase();

		// ************* 步骤 4：拼接 Authorization *************
		String authorization = algorithm + " " + "Credential=" + tencentSecretId + "/" + credentialScope + ", "
				+ "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature;

		TreeMap<String, String> headers = new TreeMap<String, String>();
		headers.put("Authorization", authorization);
		headers.put("Content-Type", CT_JSON);
		headers.put("Host", host);
		headers.put("X-TC-Action", action);
		headers.put("X-TC-Timestamp", timestamp);
		headers.put("X-TC-Version", version);
		headers.put("X-TC-Region", region);
		return headers;
	}

	public void amazonEmail(SendEmailMessage message) {
		log.info("start amazon email send, {}", message.toString());
		if (StringUtils.isBlank(message.getToEmail())) {
			log.info("amazon email send error, email is null");
			return;
		}
		try {
			Session session = Session.getDefaultInstance(getProperties());
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from, fromName));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(message.getToEmail()));
			msg.setSubject(subject);
			String context = editContext(message.getType(), message.getCode(), message.getContent());
			msg.setContent(context, "text/html;charset=UTF-8");
			Transport transport = session.getTransport();
			// Connect to Amazon SES using the SMTP username and password you specified
			// above.
			transport.connect(host, smtpUsername, smtpPassword);
			// Send the email.
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
			if (message.getType() == EmailTypeEnum.RESET_PASSWORD || message.getType() == EmailTypeEnum.VERIFY_CODE) {
				String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, message.getToEmail());
				redisTemplate.opsForValue().set(key, message.getCode());
				redisTemplate.expire(key, expiredSeconds, TimeUnit.SECONDS);
				log.info("AmazonEmailService|sendCode|{}", message.getCode());
			}
		} catch (Exception ex) {
			log.error("The amazon email was not sent. Error message: " + ex.getMessage());
		}
	}

	private Properties getProperties() {
		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		return props;
	}

	private String editContext(EmailTypeEnum type, String code, String content) {
		String message = "";
		switch (type) {
		case REGISTER:
			message = HtmlUtil.readInputStream(ENGLISH_REGISTER_HTML_BODY);
			break;
		case VERIFY_CODE:
		case RESET_PASSWORD:
			message = String.format(HtmlUtil.readInputStream("email/" + codeHtml), code);
			break;
		case CUSTOM_CONTENT:
			message = String.format(HtmlUtil.readInputStream(ENGLISH_CONTENT_HTML_BODY), content);
			break;
		case SPECIFIC_CONTENT:
			message = HtmlUtil.readInputStream(ENGLISH_SPECIFIC_CONTENT_HTML_BODY);
			break;
		default:
			message += "Welcome to merlin solar!";
			break;
		}
		return message;
	}

}
