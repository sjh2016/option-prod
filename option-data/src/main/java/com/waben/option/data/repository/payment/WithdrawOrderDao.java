package com.waben.option.data.repository.payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.waben.option.common.model.dto.payment.PaymentPassagewayDaySummaryDTO;
import com.waben.option.common.model.dto.payment.PlatformPaymentDayStatisticsDTO;
import com.waben.option.common.model.dto.payment.UserPaymentStatisticsDTO;
import com.waben.option.common.model.dto.payment.WithdrawAdminStaDTO;
import com.waben.option.common.model.dto.payment.WithdrawOrderDTO;
import com.waben.option.common.model.dto.summary.FundDataDTO;
import com.waben.option.common.model.dto.summary.WithdrawAmountDTO;
import com.waben.option.common.model.request.payment.WithdrawAdminPageRequest;
import com.waben.option.data.entity.payment.WithdrawOrder;
import com.waben.option.data.repository.BaseRepository;

@Mapper
public interface WithdrawOrderDao extends BaseRepository<WithdrawOrder> {

    public WithdrawAdminStaDTO adminSta(WithdrawAdminPageRequest req);
    
	@Select({ "<script>", "select t1.*, t2.uid, t2.username, t2.total_recharge_amount as totalRechargeAmount, t2.total_withdraw_amount as totalWithdrawAmount, t2.invite_count as invitePeople, t2.invite_recharge_count as inviteRechargePeople from t_u_withdraw_order t1 ",
			"left join t_u_user_sta t2 on t1.user_id=t2.id ",
			"<where><if test='cashType != null'>and t1.cash_type=#{cashType} </if>",
			"<if test='orderNo != null'>and t1.order_no=#{orderNo} </if>",
			"<if test='thirdOrderNo != null'>and t1.third_order_no=#{thirdOrderNo} </if>",
			"<if test='payApiId != null'>and t1.pay_api_id=#{payApiId} </if>",
			"<if test='status != null'>and t1.status=#{status} </if>",
			"<if test='uidList != null and uidList.size > 0'> and t1.user_id in ",
			"<foreach collection='uidList' item='userId' open='(' separator=',' close=')'>#{userId}</foreach> </if>",
			"<if test='brokerSymbolList != null and brokerSymbolList.size > 0'> and t1.broker_symbol in ",
			"<foreach collection='brokerSymbolList' item='brokerSymbol' open='(' separator=',' close=')'>#{brokerSymbol}</foreach> </if>",
			"<if test='startTime != null'>and t1.gmt_create&gt;=#{startTime} </if>",
			"<if test='endTime != null'>and t1.gmt_create&lt;#{endTime} </if>",
			"<if test='arrivalStart != null'>and t1.arrival_time&gt;=#{arrivalStart} </if>",
			"<if test='arrivalEnd != null'>and t1.arrival_time&lt;#{arrivalEnd} </if>",
			"<if test='mobilePhone != null'>and t1.mobile_phone=#{mobilePhone} </if>",
			"<if test='isBlack != null'>and t2.is_black=#{isBlack} </if></where>",
			"order by t1.gmt_create desc limit #{limit},#{size}", "</script>" })
	public List<WithdrawOrderDTO> adminPage(WithdrawAdminPageRequest req);
    
    @Select({ "<script>", "select t1.*, t2.username, t2.total_recharge_amount as totalRechargeAmount, t2.total_withdraw_amount as totalWithdrawAmount, t2.invite_real_count as invitePeople, t2.invite_recharge_count as inviteRechargePeople from t_u_withdraw_order t1 ",
		"left join t_u_user_sta t2 on t1.user_id=t2.id ",
		"<where><if test='cashType != null'>and t1.cash_type=#{cashType} </if>",
		"<if test='orderNo != null'>and t1.order_no=#{orderNo} </if>",
		"<if test='thirdOrderNo != null'>and t1.third_order_no=#{thirdOrderNo} </if>",
		"<if test='payApiId != null'>and t1.pay_api_id=#{payApiId} </if>",
		"<if test='status != null'>and t1.status=#{status} </if>",
		"<if test='uidList != null and uidList.size > 0'> and t1.user_id in ",
		"<foreach collection='uidList' item='userId' open='(' separator=',' close=')'>#{userId}</foreach> </if>",
		"<if test='brokerSymbolList != null and brokerSymbolList.size > 0'> and t1.broker_symbol in ",
		"<foreach collection='brokerSymbolList' item='brokerSymbol' open='(' separator=',' close=')'>#{brokerSymbol}</foreach> </if>",
		"<if test='startTime != null'>and t1.gmt_create&gt;=#{startTime} </if>",
		"<if test='endTime != null'>and t1.gmt_create&lt;#{endTime} </if>",
		"<if test='arrivalStart != null'>and t1.arrival_time&gt;=#{arrivalStart} </if>",
		"<if test='arrivalEnd != null'>and t1.arrival_time&lt;#{arrivalEnd} </if>",
		"<if test='mobilePhone != null'>and t1.mobile_phone=#{mobilePhone} </if>",
		"<if test='isBlack != null'>and t2.is_black=#{isBlack} </if></where>",
		"order by t1.gmt_create desc limit #{limit},#{size}", "</script>" })
	public List<WithdrawOrderDTO> adminRealPage(WithdrawAdminPageRequest req);
    
    @Select({"<script>",
        "select count(*) from t_u_withdraw_order t1, t_u_user_sta t2 where t1.user_id=t2.id ",
        "<if test='cashType != null'>and t1.cash_type=#{cashType} </if>",
		"<if test='orderNo != null'>and t1.order_no=#{orderNo} </if>",
		"<if test='thirdOrderNo != null'>and t1.third_order_no=#{thirdOrderNo} </if>",
		"<if test='payApiId != null'>and t1.pay_api_id=#{payApiId} </if>",
		"<if test='status != null'>and t1.status=#{status} </if>",
		"<if test='uidList != null and uidList.size > 0'> and t1.user_id in ",
		"<foreach collection='uidList' item='userId' open='(' separator=',' close=')'>#{userId}</foreach> </if>",
		"<if test='brokerSymbolList != null and brokerSymbolList.size > 0'> and t1.broker_symbol in ",
		"<foreach collection='brokerSymbolList' item='brokerSymbol' open='(' separator=',' close=')'>#{brokerSymbol}</foreach> </if>",
		"<if test='startTime != null'>and t1.gmt_create&gt;=#{startTime} </if>",
		"<if test='endTime != null'>and t1.gmt_create&lt;#{endTime} </if>",
		"<if test='arrivalStart != null'>and t1.arrival_time&gt;=#{arrivalStart} </if>",
		"<if test='arrivalEnd != null'>and t1.arrival_time&lt;#{arrivalEnd} </if>",
		"<if test='mobilePhone != null'>and t1.mobile_phone=#{mobilePhone} </if>",
		"<if test='isBlack != null'>and t2.is_black=#{isBlack} </if>",
        "</script>"})
    public Integer adminCount(WithdrawAdminPageRequest req);

    public UserPaymentStatisticsDTO userStatistics(Long userId);

    public PlatformPaymentDayStatisticsDTO platformDayStatistics(String day);

    public List<PaymentPassagewayDaySummaryDTO> passagewayDayStatistics(String day);

    public List<PaymentPassagewayDaySummaryDTO> offlineDayStatistics(String day);

    @Select("select sum(req_num) from t_u_withdraw_order where status = 'SUCCESSFUL' and arrival_time like concat(#{time}, '%')")
    BigDecimal queryTotalWithdrawAmount(@Param("time") String time);

    @Select("select count(DISTINCT user_id) from t_u_withdraw_order where status = 'SUCCESSFUL' and arrival_time like concat(#{time}, '%')")
    Integer queryWithdrawCount(@Param("time") String time);

    @Select({"<script>",
            "SELECT user_id AS userId, SUM(real_num) AS amount FROM t_u_withdraw_order WHERE `status`='SUCCESSFUL' ",
            "<if test='uidList != null and uidList.size > 0'>",
            "and user_id in ",
            "<foreach collection='uidList' item='userId' open='(' separator=',' close=')'> ",
            "#{userId}",
            "</foreach> ",
            "</if>",
            " GROUP BY user_id ",
            "</script>"})
    List<WithdrawAmountDTO> totalWithdrawAmountByUsers(@Param("uidList") List<Long> uidList);

    @Select("SELECT " +
            "DATE_FORMAT(gmt_create,'%Y-%m-%d') AS days, " +
            "SUM(real_num) AS totalWithdrawAmount, " +
            "COUNT(id) AS withdrawCount, " +
            "COUNT(DISTINCT user_id) AS withdrawPeopleCount " +
            "FROM t_u_withdraw_order WHERE `status`='SUCCESSFUL' GROUP BY days  HAVING days= #{localDate}")
    List<FundDataDTO> fundDataStatisticsByWithdraw(@Param("localDate") LocalDate localDate);

}
