package com.waben.option.data.repository.simulate;

import org.apache.ibatis.annotations.Mapper;

import com.waben.option.data.entity.simulate.WithdrawalData;
import com.waben.option.data.repository.BaseRepository;

@Mapper
public interface WithdrawalDataDao extends BaseRepository<WithdrawalData> {
}
