package com.waben.option.core.service.activity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.dto.activity.TreasureChestProbabilityDTO;
import com.waben.option.common.model.dto.activity.TreasureChestUserJoinDTO;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.common.model.request.activity.TreasureChestOpenRequest;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.data.entity.activity.TreasureChest;
import com.waben.option.data.entity.activity.TreasureChestUserJoin;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.activity.TreasureChestDao;
import com.waben.option.data.repository.activity.TreasureChestUserJoinDao;
import com.waben.option.data.repository.user.UserDao;

@Service
public class TreasureChestService {

	@Resource
	private UserDao userDao;

	@Resource
	private TreasureChestDao treasureChestDao;

	@Resource
	private TreasureChestUserJoinDao treasureChestUserJoinDao;

	@Resource
	private AccountService accountService;

	@Resource
	private IdWorker idWorker;

	@Resource
	private ModelMapper modelMapper;

	@Resource
	private StaticConfig staticConfig;

	private Random random = new Random();

	@Transactional
	public BigDecimal open(TreasureChestOpenRequest req) {
		QueryWrapper<TreasureChest> query = new QueryWrapper<>();
		query.eq(TreasureChest.ONLINE, true);
		query.orderByAsc(TreasureChest.SORT);
		List<TreasureChest> list = treasureChestDao.selectList(query);
		if (list != null && list.size() > 0) {
			TreasureChest chest = list.get(0);
			LocalDateTime now = LocalDateTime.now();
			String day = LocalDate.now().toString();
			if (now.isAfter(chest.getStartTime()) && now.isBefore(chest.getEndTime())) {
				if (chest.getUsedQuantity().compareTo(chest.getLimitQuantity()) < 0) {
					// 判断今天是否参与过
					Integer check = treasureChestUserJoinDao.selectCount(new QueryWrapper<TreasureChestUserJoin>()
							.eq(TreasureChestUserJoin.USER_ID, req.getUserId()).eq(TreasureChestUserJoin.DAY, day));
					if (check != null && check.intValue() > 0) {
						throw new ServerException(5023);
					}
					// 验证宝箱密码
					if (!req.getPassword().trim().equals(chest.getPassword())) {
						throw new ServerException(5024);
					}
					// 开启宝箱
					BigDecimal amount = computeAmount(chest.getProbability());
					if (amount.compareTo(BigDecimal.ZERO) <= 0) {
						throw new ServerException(5022);
					}
					User user = userDao.selectById(req.getUserId());
					TreasureChestUserJoin join = new TreasureChestUserJoin();
					join.setId(idWorker.nextId());
					join.setUserId(req.getUserId());
					join.setUid(user.getUid());
					join.setAmount(amount);
					join.setDay(day);
					treasureChestUserJoinDao.insert(join);
					chest.setUsedQuantity(chest.getUsedQuantity() + 1);
					treasureChestDao.updateById(chest);
					// 奖励
					List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
					transactionBeanList.add(AccountTransactionBean.builder().userId(req.getUserId())
							.type(TransactionEnum.CREDIT_LUCKY_DRAW).amount(amount).transactionId(join.getId())
							.currency(staticConfig.getDefaultCurrency()).build());
					accountService.transaction(req.getUserId(), transactionBeanList);
					return amount;
				} else {
					// 已抢完
					throw new ServerException(5022);
				}
			} else {
				// 无进行中的活动
				throw new ServerException(5021);
			}
		} else {
			// 无进行中的活动
			throw new ServerException(5021);
		}
	}

	private BigDecimal computeAmount(String probability) {
		BigDecimal result = BigDecimal.ZERO;
		ArrayList<TreasureChestProbabilityDTO> list = JacksonUtil.decode(probability, ArrayList.class,
				TreasureChestProbabilityDTO.class);
		int randomNum = random.nextInt(10000);
		for (TreasureChestProbabilityDTO dto : list) {
			if (randomNum >= dto.getMinRandom() && randomNum < dto.getMaxRandom()) {
				if (dto.getMinAmount().compareTo(dto.getMaxAmount()) == 0) {
					result = dto.getMinAmount();
				} else {
					int between = dto.getMaxAmount().subtract(dto.getMinAmount()).intValue();
					result = dto.getMinAmount().add(new BigDecimal((random.nextInt(between) / 10) * 10));
				}
				break;
			}
		}
		return result;
	}

	public PageInfo<TreasureChestUserJoinDTO> joinPage(int page, int size) {
		QueryWrapper<TreasureChestUserJoin> query = new QueryWrapper<TreasureChestUserJoin>();
		query.orderByDesc(TreasureChestUserJoin.GMT_CREATE);
		IPage<TreasureChestUserJoin> pageData = treasureChestUserJoinDao.selectPage(new Page<>(page, size), query);
		PageInfo<TreasureChestUserJoinDTO> pageInfo = new PageInfo<>();
		if (pageData.getRecords() != null && pageData.getRecords().size() > 0) {
			pageInfo.setRecords(pageData.getRecords().stream()
					.map(temp -> modelMapper.map(temp, TreasureChestUserJoinDTO.class)).collect(Collectors.toList()));
		}
		pageInfo.setPage(page);
		pageInfo.setSize(size);
		pageInfo.setTotal(pageData.getTotal());
		return pageInfo;
	}

}
