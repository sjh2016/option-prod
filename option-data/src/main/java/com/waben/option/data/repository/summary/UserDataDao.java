package com.waben.option.data.repository.summary;

import com.waben.option.data.entity.summary.UserData;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDataDao extends BaseRepository<UserData> {
}
