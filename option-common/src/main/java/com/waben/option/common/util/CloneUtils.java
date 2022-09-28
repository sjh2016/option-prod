package com.waben.option.common.util;

import com.waben.option.common.component.SpringContext;
import com.waben.option.common.model.PageInfo;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class CloneUtils {

    public static <T> T copy(Object object, Class<T> clazz) {
        ModelMapper modelMapper = SpringContext.getBean(ModelMapper.class);
        return modelMapper.map(object, clazz);
    }

    public static <T> List<T> copy(List<?> list, Class<T> clazz) {
        ModelMapper modelMapper = SpringContext.getBean(ModelMapper.class);
        return list.stream().map(object -> modelMapper.map(object, clazz)).collect(Collectors.toList());
    }

    public static <T> PageInfo<T> copy(PageInfo<?> pageInfo, Class<T> clazz) {
        ModelMapper modelMapper = SpringContext.getBean(ModelMapper.class);
        List<T> objList = pageInfo.getRecords().stream().map(object -> modelMapper.map(object, clazz)).collect(Collectors.toList());
        PageInfo<T> info = new PageInfo<>();
        info.setPage(pageInfo.getPage());
        info.setRecords(objList);
        info.setSize(pageInfo.getSize());
        info.setTotal(pageInfo.getTotal());
        return info;
    }

}
