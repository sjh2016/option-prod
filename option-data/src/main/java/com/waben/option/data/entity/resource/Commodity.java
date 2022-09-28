package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/6/23 17:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_commodity", autoResultMap = true)
public class Commodity extends BaseEntity<Long> {

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
	 * 规格(功率)
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
	 * 是否需要退回本金
	 */
	private Boolean needReturn;
	/**
	 * 排序
	 */
	private Integer sort;
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
	 * 赠送的产品ID
	 */
	private String giveCommodityId;
	/**
	 * 是否热门投资
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

	public static final String SORT = "sort";
	public static final String ACTUAL_PRICE = "actual_price";
	public static final String ONLINE = "online";
	public static final String HOT = "hot";
}
