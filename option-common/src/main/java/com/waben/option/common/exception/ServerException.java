package com.waben.option.common.exception;

import com.waben.option.common.component.LocaleContext;
import com.waben.option.common.message.MessageFactory;

public class ServerException extends RuntimeException {

	private static final long serialVersionUID = -383438728725359688L;

	private final int code;

	private final String msg;

	public ServerException(int code) {
		super("Server Exception Business Code: " + code);
		this.code = code;
		String locale = LocaleContext.getLocale();
		String countryCode = LocaleContext.getCountryCode();
		this.msg = MessageFactory.INSTANCE.getMessage(code + "", locale, countryCode);
	}

	public ServerException(int code, String msg) {
		super("Server Exception Business Code: " + code);
		this.code = code;
		this.msg = msg;
	}

	public ServerException(int code, String... args) {
		super("Server Exception Business Code: " + code);
		this.code = code;
		String locale = LocaleContext.getLocale();
		String countryCode = LocaleContext.getCountryCode();
		this.msg = MessageFactory.INSTANCE.getMessage(code + "", locale, countryCode, args);
	}

	public ServerException(BusinessErrorConstants constants) {
		super("Server Exception Business Code: " + constants.getCode());
		this.code = constants.getCode();
		String locale = LocaleContext.getLocale();
		String countryCode = LocaleContext.getCountryCode();
		this.msg = MessageFactory.INSTANCE.getMessage(code + "", locale, countryCode);
	}

	public ServerException(BusinessErrorConstants constants, String msg) {
		super("Server Exception Business Code: " + constants.getCode());
		this.code = constants.getCode();
		this.msg = msg;
	}

	public ServerException(BusinessErrorConstants constants, String... args) {
		super("Server Exception Business Code: " + constants.getCode());
		this.code = constants.getCode();
		String locale = LocaleContext.getLocale();
		String countryCode = LocaleContext.getCountryCode();
		this.msg = MessageFactory.INSTANCE.getMessage(code + "", locale, countryCode, args);
	}

	public int getCode() {
		return this.code;
	}

	public String getMsg() {
		return msg;
	}

	public String toString() {
		return "{\"code\":" + code + ", \"msg\":\"" + msg + "\"}";
	}

}
