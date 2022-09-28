package com.waben.option.core.service.points;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.waben.option.common.amqp.AMQPMessage;
import com.waben.option.common.amqp.message.RunOrderSettlementMessage;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.point.PointMerchantDTO;
import com.waben.option.common.model.dto.point.PointProductDTO;
import com.waben.option.common.model.dto.point.PointRunOrderDynamicDTO;
import com.waben.option.common.model.enums.RunOrderStatusEnum;
import com.waben.option.common.model.request.point.PointRunUserOrderRequest;
import com.waben.option.common.service.AMQPService;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.common.util.NumberUtil;
import com.waben.option.data.entity.point.PointRunOrder;
import com.waben.option.data.entity.point.PointRunOrderDynamic;
import com.waben.option.data.entity.resource.Config;
import com.waben.option.data.repository.point.PointRunOrderDynamicDao;
import com.waben.option.data.repository.resource.ConfigDao;

@Service
public class PointRunOrderDynamicService {

	@Resource
	private ModelMapper modelMapper;

	@Resource
	private PointRunOrderDynamicDao pointRunOrderDynamicDao;

	@Resource
	private PointMerchantService pointMerchantService;

	@Resource
	private PointProductService pointProductService;

	@Resource
	private IdWorker idWorker;

	@Resource
	private ConfigDao configDao;

	@Resource
	private AMQPService amqpService;

	private Random random = new Random();

	@Value("${generateDynamicSize:20}")
	private int generateDynamicSize;

	public PageInfo<PointRunOrderDynamicDTO> page(PointRunUserOrderRequest req) {
		QueryWrapper<PointRunOrderDynamic> query = new QueryWrapper<>();
		if (req.getStatusList() != null && req.getStatusList().size() == 1) {
			query.eq(PointRunOrder.STATUS, req.getStatusList().get(0).name());
			if (req.getStatusList().get(0) == RunOrderStatusEnum.PENDING) {
				query.gt(PointRunOrder.EXPIRE_TIME, LocalDateTime.now());
			}
		} else if (req.getStatusList() != null && req.getStatusList().size() > 1) {
			query.in(PointRunOrder.STATUS, req.getStatusList());
		}
		query.orderByDesc(PointRunOrderDynamic.RUN_TIME);
		IPage<PointRunOrderDynamic> pageWrapper = pointRunOrderDynamicDao
				.selectPage(new Page<>(req.getPage(), req.getSize()), query);
		PageInfo<PointRunOrderDynamicDTO> pageInfo = new PageInfo<>();
		pageInfo.setRecords(pageWrapper.getRecords().stream()
				.map(temp -> modelMapper.map(temp, PointRunOrderDynamicDTO.class)).collect(Collectors.toList()));
		pageInfo.setPage((int) pageWrapper.getPages());
		pageInfo.setSize((int) pageWrapper.getSize());
		pageInfo.setTotal(pageWrapper.getTotal());
		return pageInfo;
	}

	@Transactional
	public void generate() {
		List<PointProductDTO> productList = pointProductService.list();
		List<PointMerchantDTO> merchantList = pointMerchantService.list();
		List<PointRunOrderDynamic> generateList = new ArrayList<>();
		// 生成订单
		if (productList != null && productList.size() > 0 && merchantList != null && merchantList.size() > 0) {
			for (int i = 0; i < generateDynamicSize; i++) {
				PointProductDTO product = productList.get(random.nextInt(productList.size()));
				PointMerchantDTO merchant = matchMerchant(merchantList, product.getAmount());
				if (merchant != null) {
					PointRunOrderDynamic dynamic = new PointRunOrderDynamic();
					dynamic.setId(idWorker.nextId());
					dynamic.setUid(NumberUtil.generateCode(8));
					dynamic.setProductId(product.getId());
					dynamic.setProductName(product.getName());
					dynamic.setStarLevel(product.getStarLevel());
					dynamic.setAmount(product.getAmount());
					dynamic.setMerchantId(merchant.getId());
					dynamic.setMerchantName(merchant.getName());
					dynamic.setRunPoint(merchant.getRunPoint());
					dynamic.setMinRunPoint(product.getMinRunPoint());
					dynamic.setMaxRunPoint(product.getMaxRunPoint());
					dynamic.setProfit(BigDecimal.ZERO);
					dynamic.setStatus(RunOrderStatusEnum.PROCESSING);
					LocalDateTime now = LocalDateTime.now().minusSeconds(random.nextInt(30));
					dynamic.setGmtCreate(now);
					dynamic.setRunTime(now);
					dynamic.setExpireTime(getExpireTime());
					pointRunOrderDynamicDao.insert(dynamic);
					generateList.add(dynamic);
				}
			}
		}
		// 放入队列
		if (generateList.size() > 0) {
			JsonNode tradeFinishTimeConfig = queryTradeFinishTime();
			for (PointRunOrderDynamic dynamic : generateList) {
				int tradeDelayTime = computeTradeDelayTime(tradeFinishTimeConfig, dynamic.getStarLevel()) * 2;
				BigDecimal profit = computeProfit(dynamic.getAmount(), dynamic.getRunPoint());
				RunOrderSettlementMessage message = new RunOrderSettlementMessage();
				message.setIsGenerate(true);
				message.setUserId(null);
				message.setParentId(null);
				message.setProductOrderId(null);
				message.setRunOrderId(dynamic.getId());
				message.setProfit(profit);
				message.setNeedDivide(false);
				message.setProfitDivideRatio(BigDecimal.ZERO);
				message.setProfitDivide(BigDecimal.ZERO);
				AMQPMessage<RunOrderSettlementMessage> amqpMessage = new AMQPMessage<RunOrderSettlementMessage>(
						message);
				amqpMessage.setMode(AMQPService.AMQPPublishMode.CONFIRMS);
				amqpMessage.setRoutingKey(RabbitMessageQueue.QUEUE_RUN_ORDER_SETTLEMENT);
//				amqpService.convertAndSendDelay(AMQPService.AMQPPublishMode.CONFIRMS,
//						RabbitMessageQueue.getRunOrderSettlementFanoutDelayExchange(0),
//						RabbitMessageQueue.QUEUE_RUN_ORDER_SETTLEMENT, amqpMessage, tradeDelayTime);
				amqpService.sendDelay(RabbitMessageQueue.EXCHANGE_RUN_ORDER_DELAY,
						RabbitMessageQueue.QUEUE_RUN_ORDER_DELAY, amqpMessage, tradeDelayTime);
			}
		}
	}

	private int computeTradeDelayTime(JsonNode tradeFinishTimeConfig, Integer startLevel) {
		int time = 60;
		if (startLevel != null && tradeFinishTimeConfig != null) {
			JsonNode config = tradeFinishTimeConfig.get("startLevel" + startLevel);
			if (config != null) {
				int min = config.get("min").asInt();
				int max = config.get("max").asInt();
				time = min + random.nextInt(max - min);
			}
		}
		return time * 1000;
	}

	private JsonNode queryTradeFinishTime() {
		QueryWrapper<Config> query = new QueryWrapper<>();
		query.eq(Config.KEY, "tradeFinishTime");
		Config config = configDao.selectOne(query);
		if (config != null && !StringUtils.isBlank(config.getValue())) {
			return JacksonUtil.decodeToNode(config.getValue());
		}
		return null;
	}

	private LocalDateTime getExpireTime() {
		return LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0, 0));
	}

	private BigDecimal computeProfit(BigDecimal amount, BigDecimal runPoint) {
		return amount.multiply(runPoint).setScale(0, RoundingMode.DOWN);
	}

	private PointMerchantDTO matchMerchant(List<PointMerchantDTO> merchantList, BigDecimal amount) {
		PointMerchantDTO result = null;
		for (PointMerchantDTO merchant : merchantList) {
			if (amount.compareTo(merchant.getMinAmount()) >= 0 && amount.compareTo(merchant.getMaxAmount()) <= 0) {
				result = merchant;
				break;
			}
		}
		return result;
	}

	@Transactional
	public void clear() {
		pointRunOrderDynamicDao.clearYesterdayData();
	}

}
