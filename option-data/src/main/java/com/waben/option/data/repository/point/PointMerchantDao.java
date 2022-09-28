package com.waben.option.data.repository.point;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import com.waben.option.data.entity.point.PointMerchant;
import com.waben.option.data.repository.BaseRepository;

@Mapper
public interface PointMerchantDao extends BaseRepository<PointMerchant> {

	@Update("update t_d_point_merchant set used_amount=0")
	void clearSchedule();

}
