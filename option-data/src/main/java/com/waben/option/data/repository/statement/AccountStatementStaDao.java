package com.waben.option.data.repository.statement;

import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

@Mapper
public interface AccountStatementStaDao<T> extends BaseRepository<T> {

    BigDecimal querySta(Long userId, int level);

}
