package com.waben.option.common.model.enums;

public enum CurrencyEnum {

	BRL(2),

	USDT(2),
	
	USDC(2),

	AMT(2),

	CZDT(2),

	LTK(2),

	BTC(6),

	ETH(6),

	AMTC(2),

	LUCKY_MONEY(2),
	
	JPY(2),
	
	GBP(2),
	
	KRW(2),
	
	RUR(2),
	
	AUD(2),
	
	CNY(2),

	HKD(2),

	USD(2),
	
	EUR(2),
	
	PHP(2),
	
	SGD(2),
	
	VND(2),
	
	MYR(2),
	
	THB(2),
	
	INR(2),
	
	IDR(2),
	
	NZD(2),
	
	NGN(2),

	PHL(2)
	;

	private int precision;

	private CurrencyEnum(int precision) {
		this.precision = precision;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

}
