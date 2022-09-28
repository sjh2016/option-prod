package com.waben.option.core.service.resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.model.dto.resource.BankCodeDTO;
import com.waben.option.data.entity.resource.BankCode;
import com.waben.option.data.repository.resource.BankCodeDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankCodeService {

	@Resource
	private BankCodeDao bankCodeDao;

	@Resource
	private ModelMapper modelMapper;

	public List<BankCodeDTO> query(String name, String code, String currency) {
		QueryWrapper<BankCode> query = new QueryWrapper<>();
		if (name != null) {
			query.like(BankCode.NAME, name);
		}
		if (code != null) {
			query.like(BankCode.CODE, code);
		}
		if (currency != null) {
			query.eq(BankCode.CURRENCY, currency);
		}
		query.orderByAsc(BankCode.CODE);
		List<BankCode> bankCodeList = bankCodeDao.selectList(query);
		return bankCodeList.stream().map(bankCode -> modelMapper.map(bankCode, BankCodeDTO.class))
				.collect(Collectors.toList());
	}

	public BankCodeDTO query(String code) {
		QueryWrapper<BankCode> query = new QueryWrapper<>();
		query.eq(BankCode.CODE, code);
		BankCode bankCode = bankCodeDao.selectOne(query);
		return modelMapper.map(bankCode, BankCodeDTO.class);
	}

}
