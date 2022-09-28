package com.waben.option.core.service.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.model.dto.activity.AllowanceDTO;
import com.waben.option.data.entity.activity.Allowance;
import com.waben.option.data.repository.activity.AllowanceDao;

@Service
public class AllowanceService {

	@Resource
	private ModelMapper modelMapper;

	@Resource
	private AllowanceDao allowanceDao;

	public List<AllowanceDTO> query() {
		QueryWrapper<Allowance> query = new QueryWrapper<>();
		query.in(Allowance.ID, 1, 2, 3);
		List<Allowance> list = allowanceDao.selectList(query);
		if (list != null && list.size() > 0) {
			return list.stream().map(temp -> {
				AllowanceDTO dto = modelMapper.map(temp, AllowanceDTO.class);
				dto.setType(temp.getId());
				return dto;
			}).collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

}
