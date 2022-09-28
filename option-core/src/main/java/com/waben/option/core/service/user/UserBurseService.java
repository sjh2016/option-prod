package com.waben.option.core.service.user;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.dto.user.UserBurseDTO;
import com.waben.option.common.model.enums.BurseTypeEnum;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.data.entity.user.UserBurse;
import com.waben.option.data.repository.user.UserBurseDao;

@Service
public class UserBurseService {

	@Resource
	private UserBurseDao userBurseDao;

	@Resource
	private ModelMapper modelMapper;

	@Resource
	private IdWorker idWorker;

	public UserBurseDTO query(Long userId, CurrencyEnum currency, BurseTypeEnum burseType, Long payApiId) {
		QueryWrapper<UserBurse> query = new QueryWrapper<>();
		query.eq(UserBurse.USER_ID, userId);
		query.eq(UserBurse.CURRENCY, currency.name());
		query.eq(UserBurse.BURSE_TYPE, burseType.name());
		query.eq(UserBurse.PAY_API_ID, payApiId);
		UserBurse entity = userBurseDao.selectOne(query);
		if (entity != null) {
			return modelMapper.map(entity, UserBurseDTO.class);
		} else {
			return null;
		}
	}

	public UserBurseDTO queryByAddress(String address) {
		QueryWrapper<UserBurse> query = new QueryWrapper<>();
		query.eq(UserBurse.ADDRESS, address);
		UserBurse entity = userBurseDao.selectOne(query);
		if (entity != null) {
			return modelMapper.map(entity, UserBurseDTO.class);
		} else {
			return null;
		}
	}

	@Transactional
	public void create(UserBurseDTO req) {
		UserBurseDTO check = query(req.getUserId(), req.getCurrency(), req.getBurseType(), req.getPayApiId());
		if (check != null) {
			throw new ServerException(2061);
		}
		UserBurse burse = modelMapper.map(req, UserBurse.class);
		burse.setId(idWorker.nextId());
		userBurseDao.insert(burse);
	}

}
