package com.waben.option.core.service.resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.model.dto.resource.LanguageDTO;
import com.waben.option.data.entity.resource.Language;
import com.waben.option.data.repository.resource.LanguageDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Peter
 * @date: 2021/6/2 15:45
 */
@Service
public class LanguageService {

    @Resource
    private LanguageDao languageDao;

    @Resource
    private ModelMapper modelMapper;

    public List<LanguageDTO> queryList() {
        List<Language> languageList = languageDao.selectList(new QueryWrapper<Language>().eq(Language.ENABLE, true).orderByAsc(Language.SORT));
        return languageList.stream().map(language ->
                modelMapper.map(language, LanguageDTO.class)).collect(Collectors.toList());
    }

    public List<LanguageDTO> queryByCode(String code) {
        List<Language> list = languageDao.selectList(new QueryWrapper<Language>().eq(Language.CODE, code));
        return list.stream().map(language -> modelMapper.map(language, LanguageDTO.class)).collect(Collectors.toList());
    }
}
