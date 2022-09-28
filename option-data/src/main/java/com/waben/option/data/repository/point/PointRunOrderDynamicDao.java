package com.waben.option.data.repository.point;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import com.waben.option.data.entity.point.PointRunOrderDynamic;
import com.waben.option.data.repository.BaseRepository;

@Mapper
public interface PointRunOrderDynamicDao extends BaseRepository<PointRunOrderDynamic> {

	@Delete("delete from t_u_point_run_order_dynamic where status='SUCCESSFUL' and gmt_create<(CURRENT_TIMESTAMP() + INTERVAL - 30 MINUTE)")
	void clearYesterdayData();

}
