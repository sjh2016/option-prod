package com.waben.option.data.repository.user;

import org.apache.ibatis.annotations.Mapper;

import com.waben.option.data.entity.user.UserSta;
import com.waben.option.data.repository.BaseRepository;

@Mapper
public interface UserStaDao extends BaseRepository<UserSta> {

}
