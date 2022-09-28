package com.waben.option.data.repository.user;

import com.waben.option.data.entity.user.BankCard;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: Peter
 * @date: 2021/6/23 9:28
 */
@Mapper
public interface BankCardDao extends BaseRepository<BankCard> {
}
