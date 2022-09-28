package com.waben.option.data.repository.point;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.waben.option.common.model.dto.point.PointProductOrderUserStaDTO;
import com.waben.option.data.entity.point.PointProductOrder;
import com.waben.option.data.repository.BaseRepository;

@Mapper
public interface PointProductOrderDao extends BaseRepository<PointProductOrder> {

	@Update("update t_u_point_product_order set run_total_quantity=run_refresh_quantity, run_used_quantity=0 where gift=0 and status='WORKING'")
	void clearScheduleNotGift();
	
	// @Update("update t_u_point_product_order set run_total_quantity=0, run_used_quantity=0 where gift=1 and status='WORKING'")
	@Update("update t_u_point_product_order set run_total_quantity=run_refresh_quantity, run_used_quantity=0 where gift=1 and status='WORKING'")
	void clearScheduleGift();

	@Select("select sum(amount) as sumAmount, sum(total_profit) as sumProfit from t_u_point_product_order where user_id = #{userId}")
	PointProductOrderUserStaDTO userSta(@Param("userId") Long userId);

}
