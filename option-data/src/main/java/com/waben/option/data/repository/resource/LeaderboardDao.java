package com.waben.option.data.repository.resource;

import com.waben.option.data.entity.resource.Leaderboard;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: Peter
 * @date: 2021/6/23 11:37
 */
@Mapper
public interface LeaderboardDao extends BaseRepository<Leaderboard> {
}
