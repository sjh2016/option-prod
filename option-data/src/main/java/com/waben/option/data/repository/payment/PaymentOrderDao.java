package com.waben.option.data.repository.payment;

import com.waben.option.common.model.dto.payment.PaymentAdminStaDTO;
import com.waben.option.common.model.dto.payment.PaymentPassagewayDaySummaryDTO;
import com.waben.option.common.model.dto.summary.FundDataDTO;
import com.waben.option.common.model.dto.summary.WithdrawAmountDTO;
import com.waben.option.common.model.request.payment.PaymentAdminPageRequest;
import com.waben.option.data.entity.payment.PaymentOrder;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PaymentOrderDao extends BaseRepository<PaymentOrder> {

    public PaymentAdminStaDTO adminSta(PaymentAdminPageRequest req);

    public List<PaymentPassagewayDaySummaryDTO> passagewayDayStatistics(String day);

    @Select({
            "<script>",
            "select COUNT(DISTINCT user_id) as userPaymentCount from t_u_payment_order where `status`='SUCCESS' ",
            "<if test='time != null'>",
            " and gmt_update like concat(#{time}, '%')",
            "</if>",
            "<if test='uidList != null and uidList.size > 0'>",
            " AND user_id in ",
            "<foreach collection='uidList' item='uid' open='(' separator=',' close=')'>",
            "#{uid}",
            "</foreach>",
            "</if>",
            "</script>"
    })
    Integer queryOnlineCount(@Param("uidList") List<Long> uidList, @Param("time") String time);

    @Select({
            "<script>",
            "select COUNT(DISTINCT user_id) as userPaymentCount from t_u_payment_order where `status` != 'SUCCESS' ",
            "<if test='time != null'>",
            " and gmt_update like concat(#{time}, '%')",
            "</if>",
            "<if test='uidList != null and uidList.size > 0'>",
            " AND user_id in ",
            "<foreach collection='uidList' item='uid' open='(' separator=',' close=')'>",
            "#{uid}",
            "</foreach>",
            "</if>",
            "</script>"
    })
    Integer queryOnlineNotPaymentCount(@Param("uidList") List<Long> uidList, @Param("time") String time);

    @Select({
            "<script>",
            "select DISTINCT user_id as userPaymentCount from t_u_payment_order where `status` = 'SUCCESS' ",
            " and gmt_update &lt;= NOW() and gmt_update &gt;= date_sub(curdate(),interval 2 day) ",
            "<if test='uidList != null and uidList.size > 0'>",
            " AND user_id in ",
            "<foreach collection='uidList' item='uid' open='(' separator=',' close=')'>",
            "#{uid}",
            "</foreach>",
            "</if>",
            "</script>"
    })
    List<Long> queryPayment(@Param("uidList") List<Long> uidList);

    @Select({
            "<script>",
            "select DISTINCT user_id as userPaymentCount from t_u_payment_order where `status`='SUCCESS' ",
            "<if test='time != null'>",
            " and gmt_update like concat(#{time}, '%')",
            "</if>",
            "<if test='uidList != null and uidList.size > 0'>",
            " AND user_id in ",
            "<foreach collection='uidList' item='uid' open='(' separator=',' close=')'>",
            "#{uid}",
            "</foreach>",
            "</if>",
            "</script>"
    })
    List<Long> queryOnlineUserCount(@Param("uidList") List<Long> uidList, @Param("time") String time);

    @Select("select sum(real_num) from t_u_payment_order where `status` = 'SUCCESS' and arrival_time like concat(#{time}, '%')")
    BigDecimal queryPayAmountTotal(@Param("time") String time);

    @Select("select count(DISTINCT user_id)  from t_u_payment_order where `status` = 'SUCCESS' and arrival_time like concat(#{time}, '%')")
    Integer queryPayCountTotal(@Param("time") String time);

    @Select({"<script>",
            "SELECT user_id AS userId, SUM(real_num) AS amount FROM t_u_payment_order WHERE `status`='SUCCESS' ",
            "<if test='uidList != null and uidList.size > 0'>",
            "and user_id in ",
            "<foreach collection='uidList' item='userId' open='(' separator=',' close=')'> ",
            "#{userId}",
            "</foreach> ",
            "</if>",
            " GROUP BY user_id ",
            "</script>"})
    List<WithdrawAmountDTO> totalRechargeAmountByUsers(List<Long> uidList);

    @Select("SELECT COUNT(DISTINCT user_id) AS inviteRechargePeople FROM t_u_payment_order  where `status`='SUCCESS' " +
            "  AND (broker_symbol = #{symbol} OR  (broker_symbol LIKE CONCAT(#{symbol},'0','%') AND (LENGTH(broker_symbol) - LENGTH(#{symbol})) <=5))")
    Integer inviteRechargePeopleBySymbol(@Param("symbol") String symbol);

    @Select({"<script>",
            "select sum(real_num) from t_u_payment_order where `status` = 'SUCCESS' and arrival_time like concat(#{time}, '%')",
            "<if test='uidList != null and uidList.size > 0'>",
            " AND user_id in ",
            "<foreach collection='uidList' item='uid' open='(' separator=',' close=')'>",
            "#{uid}",
            "</foreach>",
            "</if>",
            "</script>"})
    BigDecimal queryPayAmount(@Param("time") String time, @Param("uidList") List<Long> uidList);

    @Select("select sum(real_num) from t_u_payment_order where `status` = 'SUCCESS' and user_id = #{userId}")
    BigDecimal payAmountTotal(@Param("userId") Long userId);


    @Select("SELECT " +
            "DATE_FORMAT(gmt_create,'%Y-%m-%d') AS days, " +
            "SUM(real_money) AS totalPayAmount, " +
            "COUNT(id) AS totalPayCount, " +
            "COUNT(DISTINCT user_id) AS totalPayPeopleCount " +
            "FROM t_u_payment_order WHERE `status`='SUCCESS' GROUP BY days  HAVING days= #{localDate}")
    List<FundDataDTO> fundDataStatisticsByOrder(@Param("localDate") LocalDate localDate);
    
    @Select("SELECT " +
            "DATE_FORMAT(gmt_create,'%Y-%m-%d') AS days, " +
            "SUM(real_money) AS totalPayAmount, " +
            "COUNT(id) AS totalPayCount, " +
            "COUNT(DISTINCT user_id) AS totalPayPeopleCount " +
            "FROM t_u_payment_order WHERE `status`='SUCCESS' and is_hidden=0 GROUP BY days  HAVING days= #{localDate}")
    List<FundDataDTO> fundDataStatisticsNotHiddenByOrder(@Param("localDate") LocalDate localDate);
    
    /*********************************rebuild***************************************/
    
    @Select("select count(distinct user_id) from t_u_payment_order where status='SUCCESS' and gmt_create like concat(#{localDate}, '%')")
    Integer staPaymentPeopleCount(String localDate);
    
    @Select("select count(distinct user_id) from t_u_payment_order where status='SUCCESS' and is_hidden=0 and gmt_create like concat(#{localDate}, '%')")
    Integer staPaymentPeopleCountNotHidden(String localDate);
    
    @Select("select sum(real_num) as totalPayAmount, count(id) as totalPayCount from t_u_payment_order where status='SUCCESS' and gmt_create like concat(#{localDate}, '%')")
    FundDataDTO staPayment(String localDate);
    
    @Select("select sum(real_num) as totalPayAmount, count(id) as totalPayCount from t_u_payment_order where status='SUCCESS' and is_hidden=0 and gmt_create like concat(#{localDate}, '%')")
    FundDataDTO staPaymentNotHidden(String localDate);
    
    @Select("select count(distinct user_id) from t_u_withdraw_order where status='SUCCESSFUL' and arrival_time like concat(#{localDate}, '%')")
    Integer staWithdrawPeopleCount(String localDate);
    
    @Select("select sum(real_num) as totalWithdrawAmount, count(id) as withdrawCount from t_u_withdraw_order where status='SUCCESSFUL' and arrival_time like concat(#{localDate}, '%')")
    FundDataDTO staWithdraw(String localDate);
    
}
