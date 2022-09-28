package com.waben.option.core.service.points;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.exception.BusinessErrorConstants;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.point.PointMerchantDTO;
import com.waben.option.common.model.request.point.PointMerchantRequest;
import com.waben.option.data.entity.point.PointMerchant;
import com.waben.option.data.repository.point.PointMerchantDao;

@Service
public class PointMerchantService {

	@Resource
	private PointMerchantDao pointMerchantDao;

	@Resource
	private IdWorker idWorker;

	@Resource
	private ModelMapper modelMapper;

	/**
	 * 商家列表
	 */
	public List<PointMerchantDTO> list() {
		QueryWrapper<PointMerchant> query = new QueryWrapper<>();
		query.eq(PointMerchant.ONLINE, true);
		query.orderByAsc(PointMerchant.MIN_AMOUNT);
		query.orderByAsc(PointMerchant.SORT);
		List<PointMerchant> list = pointMerchantDao.selectList(query);
		return list.stream().map(temp -> modelMapper.map(temp, PointMerchantDTO.class)).collect(Collectors.toList());
	}

	/**
	 * 分页获取商家
	 */
	public PageInfo<PointMerchantDTO> page(int page, int size) {
		QueryWrapper<PointMerchant> query = new QueryWrapper<>();
		query.orderByAsc(PointMerchant.MIN_AMOUNT).orderByAsc(PointMerchant.SORT);
		IPage<PointMerchant> pageWrapper = pointMerchantDao.selectPage(new Page<>(page, size), query);
		PageInfo<PointMerchantDTO> pageInfo = new PageInfo<>();
		pageInfo.setRecords(pageWrapper.getRecords().stream().map(temp -> modelMapper.map(temp, PointMerchantDTO.class))
				.collect(Collectors.toList()));
		pageInfo.setPage((int) pageWrapper.getPages());
		pageInfo.setSize((int) pageWrapper.getSize());
		pageInfo.setTotal(pageWrapper.getTotal());
		return pageInfo;
	}

	/**
	 * 新增商家
	 */
	public PointMerchantDTO create(PointMerchantRequest request) {
		PointMerchant entity = modelMapper.map(request, PointMerchant.class);
		entity.setId(idWorker.nextId());
		entity.setUsedAmount(BigDecimal.ZERO);
		pointMerchantDao.insert(entity);
		return modelMapper.map(entity, PointMerchantDTO.class);
	}

	/**
	 * 修改商家
	 */
	public PointMerchantDTO update(PointMerchantRequest request) {
		PointMerchant entity = pointMerchantDao.selectById(request.getId());
		if (entity == null) {
			throw new ServerException(BusinessErrorConstants.ERROR_DATA_NOT_FOUND);
		}
		entity.setName(request.getName());
		entity.setLogo(request.getLogo());
		entity.setMinAmount(request.getMinAmount());
		entity.setMaxAmount(request.getMaxAmount());
		entity.setRunPoint(request.getRunPoint());
		entity.setUsdtPrice(request.getUsdtPrice());
		entity.setTurnoverRate(request.getTurnoverRate());
		entity.setLimitAmount(request.getLimitAmount());
		entity.setOnline(request.getOnline());
		entity.setSort(request.getSort());
		pointMerchantDao.updateById(entity);
		return modelMapper.map(entity, PointMerchantDTO.class);
	}

	/**
	 * 删除商家
	 */
	public void delete(Long id) {
		PointMerchant entity = pointMerchantDao.selectById(id);
		if (entity == null) {
			throw new ServerException(BusinessErrorConstants.ERROR_DATA_NOT_FOUND);
		}
		pointMerchantDao.deleteById(id);
	}

}
