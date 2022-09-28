package com.waben.option.data.repository.resource;

import org.apache.ibatis.annotations.Mapper;

import com.waben.option.data.entity.resource.News;
import com.waben.option.data.repository.BaseRepository;

@Mapper
public interface NewsDao extends BaseRepository<News> {
	
}
