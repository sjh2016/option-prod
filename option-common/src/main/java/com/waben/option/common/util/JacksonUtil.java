package com.waben.option.common.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class JacksonUtil {
	private static final Logger logger = LoggerFactory.getLogger(JacksonUtil.class);
	public static final ObjectMapper objectMapper = new ObjectMapper();
	static {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		objectMapper.setDateFormat(dateFormat);
		// 是否使用parser是否允许使用java注释样式 / /**
		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		objectMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
		objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
		SimpleModule se = new SimpleModule();
		se.addSerializer(BigDecimal.class, new BigDecimalSerializer());
		objectMapper.registerModule(se);
	}

	public static class BigDecimalSerializer extends JsonSerializer<BigDecimal> {
		@Override
		public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers)
				throws IOException, JsonProcessingException {
			if (value == null) {
				value = BigDecimal.ZERO;
			}
			gen.writeNumber(value.stripTrailingZeros());
		}
	}

	public static JsonNode decodeToNode(String json) {
		try {
			return objectMapper.readTree(json);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 *
	 * @Title: decode @Description: json字符串转对象 @author yuyidi0630@163.com @param
	 *         json @param valueType @param @return @return T @throws
	 */
	public static <T> T decode(String json, Class<T> valueType) {
		try {
			return objectMapper.readValue(json, valueType);
		} catch (JsonParseException e) {
			logger.error("json decode 实体对象 解析异常{}", e.getMessage());
		} catch (JsonMappingException e) {
			logger.error("json decode 实体对象  mapping 异常{}", e.getMessage());
		} catch (IOException e) {
			logger.error("json decode 实体对象  io 异常{}", e.getMessage());
		}
		return null;
	}

	public static <T> T decode(String json, JavaType valueType) {
		try {
			return objectMapper.readValue(json, valueType);
		} catch (JsonParseException e) {
			logger.error("json decode 实体对象 解析异常{}", e.getMessage());
		} catch (JsonMappingException e) {
			logger.error("json decode 实体对象  mapping 异常{}", e.getMessage());
		} catch (IOException e) {
			logger.error("json decode 实体对象  io 异常{}", e.getMessage());
		}
		return null;
	}

	/**
	 *
	 * @Title: decode @Description: json数组 转javaList @author
	 *         yuyidi0630@163.com @param json @param
	 *         valueTypeRef @param @return @return T @throws
	 */
	public static <T> T decode(String json, TypeReference<T> valueTypeRef) {
		try {
			return objectMapper.readValue(json, valueTypeRef);
		} catch (JsonParseException e) {
			logger.error("json decode 集合 解析异常{}", e);
		} catch (JsonMappingException e) {
			logger.error("json decode 集合  mapping 异常{}", e);
		} catch (IOException e) {
			logger.error("json decode 集合  io 异常{}", e);
		}
		return null;
	}

	public static <T> T decode(String json, Class<?> collectionClass, Class<?>... elementClasses) {
		try {
			return objectMapper.readValue(json,
					objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses));
		} catch (JsonParseException e) {
			logger.error("json decode 集合 解析异常{}", e);
		} catch (JsonMappingException e) {
			logger.error("json decode 集合  mapping 异常{}", e);
		} catch (IOException e) {
			logger.error("json decode 集合  io 异常{}", e);
		}
		return null;
	}

	/**
	 *
	 * @Title: encode @Description: 对象转数组 @author yuyidi0630@163.com @param
	 *         object @return String @throws
	 */
	public static String encode(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonParseException e) {
			logger.error("json encode 实体对象 解析异常{}", e.getMessage());
		} catch (JsonMappingException e) {
			logger.error("json encode 实体对象  mapping 异常{}", e.getMessage());
		} catch (IOException e) {
			logger.error("json encode 实体对象  io 异常{}", e.getMessage());
		}
		return null;
	}

	public static JavaType getSimpleType(Class<?> clazz) {
		return objectMapper.getTypeFactory().constructSimpleType(clazz, null);
	}

	public static JavaType getGenericType(Class<?> outClass, Class<?>... elementClass) {
		return objectMapper.getTypeFactory().constructParametricType(outClass, elementClass);
	}

	public static JavaType getGenericType(Class<?> outClass, JavaType... elementType) {
		return objectMapper.getTypeFactory().constructParametricType(outClass, elementType);
	}

}
