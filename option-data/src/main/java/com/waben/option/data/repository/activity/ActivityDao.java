package com.waben.option.data.repository.activity;

import org.apache.ibatis.annotations.Mapper;

import com.waben.option.data.entity.activity.Activity;
import com.waben.option.data.repository.BaseRepository;

@Mapper
public interface ActivityDao extends BaseRepository<Activity> {

}
