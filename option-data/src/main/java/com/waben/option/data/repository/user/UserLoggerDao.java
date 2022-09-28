package com.waben.option.data.repository.user;

import com.waben.option.data.entity.user.UserLogger;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserLoggerDao extends BaseRepository<UserLogger> {

}
