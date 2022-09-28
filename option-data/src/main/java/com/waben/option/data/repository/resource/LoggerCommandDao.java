package com.waben.option.data.repository.resource;

import com.waben.option.data.entity.resource.LoggerCommand;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoggerCommandDao extends BaseRepository<LoggerCommand> {
}
