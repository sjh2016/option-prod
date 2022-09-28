package com.waben.option.core.service.resource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.activity.ActivityDTO;
import com.waben.option.common.model.dto.activity.ActivityUserJoinDTO;
import com.waben.option.common.model.dto.activity.UpdateJoinDTO;
import com.waben.option.common.model.dto.resource.SunshineDTO;
import com.waben.option.common.model.enums.ActivityJoinLimitEnum;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.enums.ActivityUserJoinStatusEnum;
import com.waben.option.common.model.enums.SunshineTypeEnum;
import com.waben.option.common.model.request.resource.SunshineRequest;
import com.waben.option.core.service.activity.ActivityService;
import com.waben.option.core.service.user.UserMissionCompleteService;
import com.waben.option.data.entity.resource.Sunshine;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.resource.SunshineDao;
import com.waben.option.data.repository.user.UserDao;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: Peter
 * @date: 2021/7/16 17:45
 */
@RefreshScope
@Slf4j
@Service
public class SunshineService {

	@Resource
	private IdWorker idWorker;

	@Resource
	private UserDao userDao;

	@Resource
	private SunshineDao sunshineDao;

	@Resource
	private UserMissionCompleteService userMissionCompleteService;

	@Resource
	private ActivityService activityService;

	@Value("${shareAutoAudit:false}")
	private Boolean shareAutoAudit;

	private DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Resource
	private ModelMapper modelMapper;

	public PageInfo<SunshineDTO> queryPage(Long userId, String enable, SunshineTypeEnum type, LocalDate localDate,
			int page, int size) {
		long startTime = System.currentTimeMillis();
		QueryWrapper<Sunshine> query = new QueryWrapper<>();
		if (userId != null) {
			query = query.eq(Sunshine.USER_ID, userId);
		}
		if (type != null) {
			query = query.eq(Sunshine.TYPE, type);
		}
		if (!StringUtils.isEmpty(enable)) {
			switch (enable) {
			case "0":
				query = query.eq(Sunshine.ENABLE, Boolean.FALSE);
				break;
			case "1":
				query = query.eq(Sunshine.ENABLE, Boolean.TRUE);
				break;
			case "2":
				query = query.isNull(Sunshine.ENABLE);
				break;
			case "3":
				// 用户查询
				break;
			}
		}
		if (!"3".equals(enable)) {
			query.apply("current_url_size>=url_size", "");
		}
		if (localDate != null) {
			query = query.likeRight(Sunshine.GMT_CREATE, localDate);
		}
		query = query.orderByDesc(Sunshine.GMT_UPDATE);
		IPage<Sunshine> iPage = sunshineDao.selectPage(new Page<>(page, size), query);
		PageInfo<SunshineDTO> pageInfo = new PageInfo<>();
		if (iPage.getTotal() > 0) {
			pageInfo.setRecords(iPage.getRecords().stream()
					.map(sunshine -> modelMapper.map(sunshine, SunshineDTO.class)).collect(Collectors.toList()));
			pageInfo.setPage(page);
			pageInfo.setSize(size);
			pageInfo.setTotal(iPage.getTotal());
		}
		log.info("sunshine page query time:" + (System.currentTimeMillis() - startTime));
		return pageInfo;
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void createOrUpdate(SunshineRequest request) {
		Sunshine selectOne = sunshineDao
				.selectOne(new QueryWrapper<Sunshine>().eq(Sunshine.USER_ID, request.getUserId())
						.eq(Sunshine.TYPE, request.getType()).likeRight(Sunshine.GMT_CREATE, LocalDate.now()));
		if (selectOne != null) {
			if (selectOne.getEnable() != null && selectOne.getEnable())
				throw new ServerException(5003);
			if (request.getEnable() != null) {
				selectOne.setEnable(request.getEnable());
				if (request.getEnable()) {
					// userMissionCompleteService.create(UserMissionRequest.builder()
					// .activityType(ActivityTypeEnum.valueOf(request.getType().name())).userId(request.getUserId()).finishCount(BigDecimal.ONE).build());
					UpdateJoinDTO updateJoinDTO = new UpdateJoinDTO();
					updateJoinDTO.setType(ActivityTypeEnum.valueOf(request.getType().name()));
					updateJoinDTO.setUserId(request.getUserId());
					updateJoinDTO.setQuantity(BigDecimal.ONE);
					activityService.updateJoin(updateJoinDTO);
				}
			} else {
				selectOne.setUrl(request.getUrl());
			}
			sunshineDao.updateById(selectOne);
		} else {
			if (request.getId() != null)
				throw new ServerException(1001);
			Sunshine sunshine = modelMapper.map(request, Sunshine.class);
			User user = userDao.selectById(request.getUserId());
			sunshine.setId(idWorker.nextId());
			sunshine.setLocalDate(LocalDate.now().toString());
			sunshine.setUsername(user == null ? "" : user.getUsername());
			sunshineDao.insert(sunshine);
		}
	}

	@Transactional
	public void audit(SunshineRequest request) {
		Sunshine entity = sunshineDao.selectById(request.getId());
		if (entity.getEnable() != null) {
			throw new ServerException(5003);
		}
		if (request.getEnable()) {
			Integer urlSize = entity.getUrlSize();
			if (entity.getUrl() == null || entity.getUrl().split(",").length < urlSize) {
				throw new ServerException(6013, new String[] { String.valueOf(urlSize) });
			}
		}
		entity.setEnable(request.getEnable());
		sunshineDao.updateById(entity);

		if (request.getEnable()) {
			UpdateJoinDTO updateJoinDTO = new UpdateJoinDTO();
			updateJoinDTO.setType(ActivityTypeEnum.valueOf(entity.getType().name()));
			updateJoinDTO.setUserId(entity.getUserId());
			updateJoinDTO.setQuantity(BigDecimal.ONE);
			activityService.updateJoin(updateJoinDTO);
		}
	}

	@Transactional
	public void upload(Long userId, SunshineTypeEnum type, String url) {
		ActivityDTO activity = activityService.queryActivity(ActivityTypeEnum.valueOf(type.name()));
		if (activity == null || !activity.getEnable()) {
			throw new ServerException(5009);
		}
		boolean autoAudit = false;
		if (activity.getAutoAudit() != null && activity.getAutoAudit()) {
			autoAudit = true;
		}
		List<ActivityUserJoinDTO> joinList = activityService.joinStatusList(userId, activity.getType());
		ActivityUserJoinDTO join = null;
		if (joinList.size() > 0) {
			join = joinList.get(joinList.size() - 1);
		}
		if (join != null && activity.getJoinLimit() == ActivityJoinLimitEnum.FOREVER_ONE
				&& join.getCurrentQuantity().compareTo(join.getTargetQuantity()) >= 0) {
			throw new ServerException(5017);
		}
		if (join != null && activity.getJoinLimit() == ActivityJoinLimitEnum.DAILY_LIMIT
				&& join.getCurrentQuantity().compareTo(join.getTargetQuantity()) >= 0) {
			throw new ServerException(5018);
		}
		// 判断时间间隔
		if (join != null && join.getStatus() == ActivityUserJoinStatusEnum.WAITING_RECEIVE) {
			throw new ServerException(5020);
		}
		if (join != null && activity.getJoinTimeInterval() != null && activity.getJoinTimeInterval() > 0) {
			LocalDateTime lastReceiveTime = join.getReceiveTime();
			if (lastReceiveTime != null
					&& lastReceiveTime.plusMinutes(activity.getJoinTimeInterval()).isAfter(LocalDateTime.now())) {
				throw new ServerException(5019, new String[] { new BigDecimal(activity.getJoinTimeInterval())
						.divide(new BigDecimal(60), 2, RoundingMode.DOWN).stripTrailingZeros().toPlainString() });
			}
		}
		// 添加或者修改
		String day = LocalDate.now().format(dayFormatter);
		List<Sunshine> entityList = sunshineDao.selectList(new QueryWrapper<Sunshine>().eq(Sunshine.USER_ID, userId)
				.eq(Sunshine.TYPE, type).isNull(Sunshine.ENABLE).orderByDesc(Sunshine.GMT_UPDATE));

		if (entityList != null && entityList.size() > 0) {
			Sunshine entity = entityList.get(0);
			entity.setUrl(url);
			entity.setUrlSize(activity.getUrlSize());
			entity.setCurrentUrlSize(url.split(",").length);
			if (autoAudit) {
				entity.setEnable(true);
			}
			sunshineDao.updateById(entity);
		} else {
			User user = userDao.selectById(userId);
			Sunshine entity = new Sunshine();
			entity.setUserId(userId);
			entity.setUsername(user.getUsername());
			entity.setType(type);
			entity.setUrl(url);
			entity.setUrlSize(activity.getUrlSize());
			entity.setCurrentUrlSize(url.split(",").length);
			entity.setLocalDate(day);
			entity.setEnable(null);
			if (autoAudit) {
				entity.setEnable(true);
			}
			sunshineDao.insert(entity);
		}
		if (autoAudit) {
			UpdateJoinDTO updateJoinDTO = new UpdateJoinDTO();
			updateJoinDTO.setType(ActivityTypeEnum.valueOf(type.name()));
			updateJoinDTO.setUserId(userId);
			updateJoinDTO.setQuantity(BigDecimal.ONE);
			activityService.updateJoin(updateJoinDTO);
		}
	}

}
