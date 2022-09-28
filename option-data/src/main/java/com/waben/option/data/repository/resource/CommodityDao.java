package com.waben.option.data.repository.resource;

import com.waben.option.data.entity.resource.Commodity;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: Peter
 * @date: 2021/6/23 19:09
 */
@Mapper
public interface CommodityDao extends BaseRepository<Commodity> {
}
