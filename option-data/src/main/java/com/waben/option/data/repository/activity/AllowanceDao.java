package com.waben.option.data.repository.activity;

import org.apache.ibatis.annotations.Mapper;

import com.waben.option.data.entity.activity.Allowance;
import com.waben.option.data.repository.BaseRepository;

@Mapper
public interface AllowanceDao extends BaseRepository<Allowance> {

}
