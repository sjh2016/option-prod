package com.waben.option.data.repository.resource;

import com.waben.option.data.entity.resource.Invite;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InviteDao extends BaseRepository<Invite> {
}
