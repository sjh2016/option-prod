package com.waben.option.common.model.dto.resource;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/6/23 19:17
 */
@Data
public class CommodityDTO {

	private Long id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 类型
	 * <ul>
	 * <li>1 移动设备</li>
	 * <li>2 电动汽车</li>
	 * </ul>
	 */
	private Integer type;

	/**
	 * 照片
	 */
	private String imgUrl;

	/**
	 * 周期
	 */
	private Integer cycle;

	/**
	 * 返点比例
	 */
	private BigDecimal returnRate;

	/**
	 * 每日收益
	 */
	private BigDecimal income;

	/**
	 * 规格（功率）
	 */
	private String specification;

	/**
	 * 价格
	 */
	private BigDecimal actualPrice;

	/**
	 * 原价
	 */
	private BigDecimal originalPrice;

	/**
	 * 数量
	 */
	private BigDecimal panelVolume;

	/**
	 * 多语言
	 */
	private Integer code;

	/**
	 * 是否启用
	 */
	private Boolean enable;
	/**
	 * 是否已售罄
	 */
	private Boolean soldOut;
	/**
	 * 是否上线
	 */
	private Boolean online;
	/**
	 * 当日可交易总数，为0表示不限制
	 */
	private Integer totalQuantity;
	/**
	 * 当日已交易数量，凌晨需置零
	 */
	private Integer usedQuantity;
	/**
	 * 产品可交易总数，为0表示不限制
	 */
	private Integer productTotalQuantity;
	/**
	 * 产品已交易数量
	 */
	private Integer productUsedQuantity;
	/**
	 * 是否热门推荐
	 */
	private Boolean hot;
	/**
	 * 是否推荐投资
	 */
	private Boolean recommend;
	/**
	 * 是否优选
	 */
	private Boolean perfect;
}
