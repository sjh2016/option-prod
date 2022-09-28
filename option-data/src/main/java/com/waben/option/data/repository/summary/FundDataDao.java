package com.waben.option.data.repository.summary;

import com.waben.option.data.entity.summary.FundData;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FundDataDao extends BaseRepository<FundData> {
}
