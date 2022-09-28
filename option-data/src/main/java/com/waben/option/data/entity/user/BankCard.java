package com.waben.option.data.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_u_bank_card")
public class BankCard extends BaseEntity<Long> {

	/** 用户ID */
	private Long userId;
	/** 银行卡号 */
	private String bankCardId;
	/** 姓名 */
	private String name;
	/** 手机号码 */
	private String mobilePhone;
	/** 身份证号 */
	private String idCard;
	/** 所属银行名称 */
	private String bankName;
	/** 所属银行代码 */
	private String bankCode;
	/** 省份代码 */
	private String provinceCode;
	/** 省份名称 */
	private String provinceName;
	/** 城市代码 */
	private String cityCode;
	/** 城市名称 */
	private String cityName;
	/** 所属支行名称 */
	private String branchName;
	/** 所属支行代码 */
	private String branchCode;
	
	public static final String USER_ID = "user_id";
	
	public static final String BANK_CARD_ID = "bank_card_id";
	
	public static final String NAME = "name";
	
	public static final String MOBILE_PHONE = "mobile_phone";
	
	public static final String ID_CARD = "id_card";
	
	public static final String BANK_NAME = "bank_name";
	
	public static final String BANK_CODE = "bank_code";
	
	public static final String PROVINCE_CODE = "province_code";
	
	public static final String PROVINCE_NAME = "province_name";
	
	public static final String CITY_CODE = "city_code";
	
	public static final String CITY_NAME = "city_name";
	
	public static final String BRANCH_NAME = "branch_name";
	
	public static final String BRANCH_CODE = "branch_code";

}
