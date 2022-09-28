package com.waben.option.data.repository.order;

import com.waben.option.common.model.dto.order.OrderTotalDTO;
import com.waben.option.common.model.dto.order.OrderUserStaDTO;
import com.waben.option.common.model.dto.summary.FundDataDTO;
import com.waben.option.data.entity.order.Order;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author: Peter
 * @date: 2021/6/23 9:58
 */
@Mapper
public interface OrderDao extends BaseRepository<Order> {

    @Select("SELECT SUM(profit) AS profit, SUM(amount * return_rate) as dayProfit FROM `t_u_order` WHERE user_id = #{userId} ")
    OrderTotalDTO orderProfit(@Param("userId") Long userId);

    @Select({"<script>", "SELECT SUM(profit) FROM `t_u_order` WHERE user_id = #{userId} ", "<when test='free!=null'>",
            " and free = 0 ", "</when>", "</script>"})
    BigDecimal queryTotalTeamIncome(@Param("userId") Long userId, @Param("free") Boolean free);

    @Select("SELECT " + "DATE_FORMAT(gmt_create,'%Y-%m-%d') AS days, "
            + "SUM(IF(free=1,profit,0)) AS freeEquipmentIncome, " + "SUM(IF(free=0,profit,0)) AS assetsIncome "
            + "FROM t_u_order GROUP BY days  HAVING days= #{localDate}")
    List<FundDataDTO> fundDataStatisticsByOrder(@Param("localDate") LocalDate localDate);

    @Select("select sum(amount) from t_u_order WHERE user_id = #{userId} and commodity_id != 1")
    BigDecimal userPlaceCount(@Param("userId") Long userId);

    @Select("select sum(amount) as sumAmount, sum(profit) as sumProfit, sum(per_profit) as perProfit from t_u_order where user_id = #{userId}")
    OrderUserStaDTO userSta(Long userId);

    @Select("select count(*) from t_u_order where user_id=#{userId} and commodity_id=1")
    Integer giveOrderCount(Long userId);

    @Select({"<script>", "SELECT SUM(sumProfit) AS sumProfit,SUM(sumAmount) AS sumAmount,SUM(perProfit) AS perProfit FROM\n" +
            "(\n" +
            "SELECT SUM(profit) AS sumProfit,SUM(actual_amount) AS sumAmount,0 AS perProfit FROM `t_u_order`\n" +
            "WHERE user_id=#{userId}\n" +
            "<if test=\"level>=1\">\n" +
            "UNION ALL\n" +
            "SELECT SUM(profit) AS sumProfit,SUM(actual_amount) AS sumAmount,0 AS perProfit FROM `t_u_order`\n" +
            "WHERE user_id IN (SELECT id FROM `t_u_user` WHERE parent_id=#{userId})\n" +
            "</if>\n" +
            "<if test=\"level>=2\">\n" +
            "UNION ALL\n" +
            "SELECT SUM(profit) AS sumProfit,SUM(actual_amount) AS sumAmount,0 AS perProfit FROM `t_u_order`\n" +
            "WHERE user_id IN (SELECT id FROM `t_u_user` WHERE parent_id IN (SELECT id FROM `t_u_user` WHERE parent_id=#{userId}))\n" +
            "</if>\n" +
            "<if test=\"level==3\">\n" +
            "UNION ALL\n" +
            "SELECT SUM(profit) AS sumProfit,SUM(actual_amount) AS sumAmount,0 AS perProfit FROM `t_u_order`\n" +
            "WHERE user_id IN (SELECT id FROM `t_u_user` WHERE parent_id IN (SELECT id FROM `t_u_user` WHERE parent_id IN (SELECT id FROM `t_u_user` WHERE parent_id=#{userId})))\n" +
            "</if>\n" +
            "UNION ALL\n" +
            "SELECT 0 AS sumProfit,0 AS sumAmount,SUM(per_profit) AS perProfit FROM `t_u_order`\n" +
            "WHERE user_id=#{userId} AND STATUS='WORKING'\n" +
            "<if test=\"level>=1\">\n" +
            "UNION ALL\n" +
            "SELECT 0 AS sumProfit,0 AS sumAmount,SUM(per_profit) AS perProfit FROM `t_u_order`\n" +
            "WHERE user_id IN (SELECT id FROM `t_u_user` WHERE parent_id=#{userId}) AND STATUS='WORKING'\n" +
            "</if>\n" +
            "<if test=\"level>=2\">\n" +
            "UNION ALL\n" +
            "SELECT 0 AS sumProfit,0 AS sumAmount,SUM(per_profit) AS perProfit FROM `t_u_order`\n" +
            "WHERE user_id IN (SELECT id FROM `t_u_user` WHERE parent_id IN (SELECT id FROM `t_u_user` WHERE parent_id=#{userId})) AND STATUS='WORKING'\n" +
            "</if>\n" +
            "<if test=\"level==3\">\n" +
            "UNION ALL\n" +
            "SELECT 0 AS sumProfit,0 AS sumAmount,SUM(per_profit) AS perProfit FROM `t_u_order`\n" +
            "WHERE user_id IN (SELECT id FROM `t_u_user` WHERE parent_id IN (SELECT id FROM `t_u_user` WHERE parent_id IN (SELECT id FROM `t_u_user` WHERE parent_id=#{userId}))) AND STATUS='WORKING'\n" +
            "</if>\n" +
            ")", "</script>"})
    public OrderUserStaDTO userStaByLevel(@Param("userId") long userId, @Param("level") int level);

    @Select({"<script>", "SELECT COALESCE(SUM(profit),0) AS sumProfit, COALESCE(SUM(actual_amount),0) AS sumAmount, COALESCE(SUM(IF(`status`='WORKING',per_profit,0)),0) AS perProfit FROM `t_u_order`\n" +
            "WHERE user_id=#{userId}", "</script>"})
    OrderUserStaDTO queryUserSta(@Param("userId") long userId);

}
