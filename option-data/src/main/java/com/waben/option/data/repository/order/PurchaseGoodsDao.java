package com.waben.option.data.repository.order;

import com.waben.option.data.entity.order.PurchaseGoods;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurchaseGoodsDao extends BaseRepository<PurchaseGoods> {
}
