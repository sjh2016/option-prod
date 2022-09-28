package com.waben.option.data.repository.simulate;

import org.apache.ibatis.annotations.Mapper;

import com.waben.option.data.entity.simulate.InvestData;
import com.waben.option.data.repository.BaseRepository;

@Mapper
public interface InvestDataDao extends BaseRepository<InvestData> {
}
