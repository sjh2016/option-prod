package com.waben.option.core.service.points;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.model.dto.point.PointProductDTO;
import com.waben.option.data.entity.point.PointProduct;
import com.waben.option.data.repository.point.PointMerchantDao;
import com.waben.option.data.repository.point.PointProductDao;
import com.waben.option.data.repository.point.PointProductOrderDao;
import com.waben.option.data.repository.point.PointRunOrderDynamicDao;

@Service
public class PointProductService {

	@Resource
	private PointProductDao pointProductDao;

	@Resource
	private PointMerchantDao pointMerchantDao;

	@Resource
	private PointProductOrderDao pointProductOrderDao;

	@Resource
	private PointRunOrderDynamicDao pointRunOrderDynamicDao;

	@Resource
	private ModelMapper modelMapper;

	public List<PointProductDTO> list() {
		QueryWrapper<PointProduct> query = new QueryWrapper<>();
		query.eq(PointProduct.ONLINE, true);
		query.eq(PointProduct.GIFT, false);
		query.orderByAsc(PointProduct.SORT);
		List<PointProduct> list = pointProductDao.selectList(query);
		return list.stream().map(temp -> modelMapper.map(temp, PointProductDTO.class)).collect(Collectors.toList());
	}

	@Transactional
	public void clearSchedule() {
		// step 1 : 产品已购买次数置0
		pointProductDao.clearSchedule();
		// step 2 : 商户已购买金额置0
		pointMerchantDao.clearSchedule();
		// step 3 : 产品订单当天已跑次数置0，刷新当天可跑次数
		pointProductOrderDao.clearScheduleNotGift();
		pointProductOrderDao.clearScheduleGift();
		// step 4 : 清空前一天的动态兑换
		pointRunOrderDynamicDao.clearYesterdayData();
	}

}
