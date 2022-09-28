package com.waben.option.common.model.dto.activity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TreasureChestProbabilityDTO {

	private BigDecimal minAmount;

	private BigDecimal maxAmount;

	private int minRandom;

	private int maxRandom;

}
