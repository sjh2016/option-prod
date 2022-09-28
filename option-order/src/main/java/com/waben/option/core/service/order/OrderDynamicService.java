package com.waben.option.core.service.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.order.OrderDynamicDTO;
import com.waben.option.common.util.NumberUtil;
import com.waben.option.data.entity.order.OrderDynamic;
import com.waben.option.data.entity.resource.Commodity;
import com.waben.option.data.repository.order.OrderDynamicDao;
import com.waben.option.data.repository.resource.CommodityDao;

@Service
public class OrderDynamicService {

	@Resource
	private IdWorker idWorker;

	@Resource
	private ModelMapper modelMapper;

	@Resource
	private OrderDynamicDao orderDynamicDao;

	@Resource
	private CommodityDao commodityDao;

	@Value("${generateDynamicCommodityRatio:2,15,3,15,4,15,5,15,6,20,7,20}")
	private String generateDynamicCommodityRatio;

	private Random random = new Random();

	public PageInfo<OrderDynamicDTO> page(int page, int size) {
		QueryWrapper<OrderDynamic> query = new QueryWrapper<>();
		query.orderByDesc(OrderDynamic.GMT_CREATE);
		IPage<OrderDynamic> pageWrapper = orderDynamicDao.selectPage(new Page<>(page, size), query);
		PageInfo<OrderDynamicDTO> pageInfo = new PageInfo<>();
		pageInfo.setRecords(pageWrapper.getRecords().stream().map(temp -> modelMapper.map(temp, OrderDynamicDTO.class))
				.collect(Collectors.toList()));
		pageInfo.setPage((int) pageWrapper.getPages());
		pageInfo.setSize((int) pageWrapper.getSize());
		pageInfo.setTotal(pageWrapper.getTotal());
		return pageInfo;
	}

	@Transactional
	public void generate(int size) {
		if (generateDynamicCommodityRatio != null && generateDynamicCommodityRatio.indexOf(",") > 0) {
			// 创建概率箱
			String[] ratioArr = generateDynamicCommodityRatio.split(",");
			ArrayList<String> probabilityBox = new ArrayList<>();
			for (int i = 0; i < ratioArr.length; i = i + 2) {
				String commodityId = ratioArr[i];
				Integer ratio = Integer.parseInt(ratioArr[i + 1]);
				for (int j = 0; j < ratio; j++) {
					probabilityBox.add(commodityId);
				}
			}
			Collections.shuffle(probabilityBox);
			// 生成订单动态
			for (int i = 0; i < size; i++) {
				Long commodityId = Long.parseLong(probabilityBox.get(random.nextInt(probabilityBox.size())));
				Commodity commodity = commodityDao.selectById(commodityId);

				OrderDynamic dynamic = new OrderDynamic();
				dynamic.setId(idWorker.nextId());
				dynamic.setUserId(null);
				dynamic.setUid(NumberUtil.generateCode(8));
				dynamic.setCommodityId(commodity.getId());
				dynamic.setCycle(commodity.getCycle());
				dynamic.setName(commodity.getName());
				dynamic.setReturnRate(commodity.getReturnRate());
				dynamic.setAmount(commodity.getOriginalPrice());
				LocalDateTime now = LocalDateTime.now().minusSeconds(random.nextInt(30));
				dynamic.setGmtCreate(now);
				dynamic.setGmtUpdate(now);
				orderDynamicDao.insert(dynamic);
			}
		}
	}

}
