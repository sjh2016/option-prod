package com.waben.option.core.service.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.CommodityDTO;
import com.waben.option.common.model.request.resource.CommodityRequest;
import com.waben.option.data.entity.resource.Commodity;
import com.waben.option.data.repository.resource.CommodityDao;

/**
 * @author: Peter
 * @date: 2021/6/23 19:10
 */
@Service
public class CommodityService {

	@Resource
	private CommodityDao commodityDao;

	@Resource
	private IdWorker idWorker;

	@Resource
	private ModelMapper modelMapper;

	public void createUpdate(CommodityRequest request) {
		Commodity commodity = modelMapper.map(request, Commodity.class);
		if (request.getId() == null) {
			commodity.setId(idWorker.nextId());
			commodityDao.insert(commodity);
		} else {
			commodityDao.updateById(commodity);
		}
	}

	public PageInfo<CommodityDTO> queryPage(int page, int size, Boolean online) {
		QueryWrapper<Commodity> query = new QueryWrapper<Commodity>().orderByAsc(Commodity.SORT);
		if (online != null) {
			query.eq(Commodity.ONLINE, online);
		}
		// query.ne(Commodity.ID, 1L);
		IPage<Commodity> commodityIPage = commodityDao.selectPage(new Page<>(page, size), query);
		PageInfo<CommodityDTO> pageInfo = new PageInfo<>();
		if (commodityIPage.getTotal() > 0) {
			pageInfo.setRecords(commodityIPage.getRecords().stream()
					.map(commodity -> modelMapper.map(commodity, CommodityDTO.class)).collect(Collectors.toList()));
			pageInfo.setPage(page);
			pageInfo.setSize(size);
			pageInfo.setTotal(commodityIPage.getTotal());
		}
		return pageInfo;
	}

	public void clearUsedQuantity() {
		List<Commodity> list = commodityDao.selectList(null);
		if (list != null && list.size() > 0) {
			for (Commodity entity : list) {
				if (entity.getUsedQuantity() != null && entity.getUsedQuantity().intValue() > 0) {
					entity.setUsedQuantity(0);
					commodityDao.updateById(entity);
				}
			}
		}
	}

	public List<CommodityDTO> hot() {
		QueryWrapper<Commodity> query = new QueryWrapper<Commodity>().orderByAsc(Commodity.SORT);
		query.eq(Commodity.HOT, true);
		List<Commodity> list = commodityDao.selectList(query);
		return list.stream().map(commodity -> modelMapper.map(commodity, CommodityDTO.class))
				.collect(Collectors.toList());
	}
}
