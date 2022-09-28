package com.waben.option.data.repository.resource;

import com.waben.option.data.entity.resource.Language;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: Peter
 * @date: 2021/6/2 15:44
 */
@Mapper
public interface LanguageDao extends BaseRepository<Language> {
}
