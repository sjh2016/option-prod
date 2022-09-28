package com.waben.option.data.repository.point;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import com.waben.option.data.entity.point.PointProduct;
import com.waben.option.data.repository.BaseRepository;

@Mapper
public interface PointProductDao extends BaseRepository<PointProduct> {

	@Update("update t_d_point_product set used_quantity=0")
	void clearSchedule();

}
