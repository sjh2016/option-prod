package com.waben.option.core.service.resource;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.model.dto.resource.AppVestDTO;
import com.waben.option.data.entity.resource.AppVest;
import com.waben.option.data.repository.resource.AppVestDao;

@Service
public class AppVestService {

	@Resource
	private AppVestDao appVestDao;

	@Resource
	private ModelMapper modelMapper;

	public AppVestDTO query(Integer type, Integer index) {
		QueryWrapper<AppVest> query = new QueryWrapper<>();
		query.eq(AppVest.TYPE, type);
		query.eq(AppVest.SHELL_INDEX, index);
		AppVest entity = appVestDao.selectOne(query);
		if (entity != null) {
			return modelMapper.map(entity, AppVestDTO.class);
		} else {
			return null;
		}
	}

}
