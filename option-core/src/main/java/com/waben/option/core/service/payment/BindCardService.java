package com.waben.option.core.service.payment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.dto.payment.BindCardDTO;
import com.waben.option.common.model.dto.resource.BankCodeDTO;
import com.waben.option.common.util.StringTemplateUtil;
import com.waben.option.core.service.resource.BankCodeService;
import com.waben.option.data.entity.payment.BindCard;
import com.waben.option.data.entity.payment.PaymentPassageway;
import com.waben.option.data.repository.payment.BindCardDao;

@Service
public class BindCardService {

	@Resource
	private BindCardDao bindCardDao;

	@Resource
	private ModelMapper modelMapper;

	@Resource
	private BankCodeService bankCodeService;

	@Resource
	private IdWorker idWorker;

	private static char delChar = '-';

	private static String regEx = "[-+ ]";

	public BindCardDTO query(Long userId, Long id) {
		BindCard bindCard = bindCardDao.selectById(id);
		if (bindCard == null) {
			throw new ServerException(1001);
		}
		if (!userId.equals(bindCard.getUserId())) {
			throw new ServerException(1001);
		}
		return modelMapper.map(bindCard, BindCardDTO.class);
	}

	public List<BindCardDTO> list(Long userId) {
		QueryWrapper<BindCard> query = new QueryWrapper<>();
		query.eq(BindCard.USER_ID, userId);
		query.orderByDesc(PaymentPassageway.GMT_CREATE);
		List<BindCard> list = bindCardDao.selectList(query);
		if (list != null && list.size() > 0) {
			return list.stream().map(temp -> modelMapper.map(temp, BindCardDTO.class)).collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

	public void bind(BindCardDTO dto) {
		BindCard bindCard = bindCardDao
				.selectOne(new QueryWrapper<BindCard>().eq(BindCard.BANK_CARD_ID, dto.getBankCardId()));
		if (bindCard != null) {
			throw new ServerException(2056);
		}
		BankCodeDTO bankCodeDTO = bankCodeService.query(dto.getBankCode());
		bindCard = new BindCard();
		bindCard.setId(idWorker.nextId());
		bindCard.setUserId(dto.getUserId());
		bindCard.setName(dto.getName().trim());
		bindCard.setBankName(bankCodeDTO.getName());
		bindCard.setBranchName(dto.getBranchName());
		bindCard.setBankCode(dto.getBankCode().trim());
		if (!StringUtils.isBlank(dto.getMobilePhone())) {
			bindCard.setMobilePhone(dto.getMobilePhone().replaceAll(regEx, "").replaceAll(" ", ""));
		}
		bindCard.setBankCardId(StringTemplateUtil.deleteString2(dto.getBankCardId(), delChar).replaceAll(" ", ""));
		bindCard.setSupportUpId(bankCodeDTO.getSupportUpId());
		bindCard.setSupportUpCode(bankCodeDTO.getSupportUpCode());
		bindCardDao.insert(bindCard);
	}

	public void update(BindCardDTO dto) {
		BindCard bindCard = bindCardDao.selectById(dto.getId());
		if (bindCard == null) {
			throw new ServerException(1001);
		}
		if (!dto.getUserId().equals(bindCard.getUserId())) {
			throw new ServerException(1001);
		}
		BankCodeDTO bankCodeDTO = bankCodeService.query(dto.getBankCode());
		bindCard.setName(dto.getName().trim());
		bindCard.setBankName(bankCodeDTO.getName());
		bindCard.setBranchName(dto.getBranchName());
		bindCard.setBankCode(dto.getBankCode().trim());
		if (!StringUtils.isBlank(dto.getMobilePhone())) {
			bindCard.setMobilePhone(dto.getMobilePhone().replaceAll(regEx, "").replaceAll(" ", ""));
		}
		bindCard.setBankCardId(StringTemplateUtil.deleteString2(dto.getBankCardId(), delChar).replaceAll(" ", ""));
		bindCard.setSupportUpId(bankCodeDTO.getSupportUpId());
		bindCard.setSupportUpCode(bankCodeDTO.getSupportUpCode());
		bindCardDao.updateById(bindCard);
	}

	public void delete(Long userId, Long id) {
		BindCard bindCard = bindCardDao.selectById(id);
		if (bindCard == null) {
			throw new ServerException(1001);
		}
		if (!userId.equals(bindCard.getUserId())) {
			throw new ServerException(1001);
		}
		bindCardDao.deleteById(id);
	}

}
