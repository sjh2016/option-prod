package com.waben.option.core.service.user;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.amqp.AMQPMessage;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.bean.TradeTransactionRemark;
import com.waben.option.common.model.bean.TransactionRemark;
import com.waben.option.common.model.dto.resource.MissionActivityDTO;
import com.waben.option.common.model.dto.resource.UserMissionCompleteDTO;
import com.waben.option.common.model.dto.user.InviteTaskAuditDTO;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.enums.InviteAuditStatusEnum;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.common.model.request.user.InviteTaskAuditRequest;
import com.waben.option.common.model.request.user.UserMissionRequest;
import com.waben.option.common.service.AMQPService;
import com.waben.option.core.amqp.message.UserMissionCompleteMessage;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.core.service.resource.MissionActivityService;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.entity.user.UserMissionComplete;
import com.waben.option.data.repository.user.UserDao;
import com.waben.option.data.repository.user.UserMissionCompleteDao;

@Service
public class UserMissionCompleteService {

	@Resource
	private IdWorker idWorker;

	@Resource
	private ModelMapper modelMapper;

	@Resource
	private UserDao userDao;

	@Resource
	private AMQPService amqpService;

	@Resource
	private AccountService accountService;

	@Resource
	private MissionActivityService missionActivityService;

	@Resource
	private UserMissionCompleteDao userMissionCompleteDao;
	
	@Resource
	private StaticConfig staticConfig;

	public List<UserMissionCompleteDTO> awardStatus(Long userId, ActivityTypeEnum activityType, LocalDate date) {
		QueryWrapper<UserMissionComplete> query = new QueryWrapper<>();
		if (userId != null) {
			query = query.eq(UserMissionComplete.USER_ID, userId);
		}
		if (activityType != null) {
			query = query.eq(UserMissionComplete.ACTIVITY_TYPE, activityType);
		}
		if (date != null) {
			query = query.likeRight(UserMissionComplete.GMT_CREATE, date);
		}
		query = query.orderByDesc(UserMissionComplete.GMT_CREATE);
		UserMissionComplete missionComplete = queryMissionComplete(userId, ActivityTypeEnum.JOIN_TG_GROUP,
				Boolean.FALSE);
		List<UserMissionComplete> completeList = userMissionCompleteDao.selectList(query);
		if (!CollectionUtils.isEmpty(completeList)) {
			if (missionComplete != null && !completeList.contains(missionComplete))
				completeList.add(missionComplete);
			return completeList.stream().map(complete -> modelMapper.map(complete, UserMissionCompleteDTO.class))
					.collect(Collectors.toList());
		}
		return null;
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void create(UserMissionRequest request) {
		synchronized (request.getUserId()) {
			if (request.getFinishCount() == null)
				request.setFinishCount(BigDecimal.ONE);
			MissionActivityDTO activity = missionActivityService.queryByType(request.getActivityType());
			UserMissionComplete userMissionComplete = queryMissionComplete(request.getUserId(),
					request.getActivityType(), activity.getDaily());
			if (userMissionComplete == null) {
				buildUserMissionComplete(request, activity);
				return;
			}
			if (activity.getLimitInviteVolume()
					&& userMissionComplete.getInviteVolume().compareTo(userMissionComplete.getMinLimitVolume()) >= 0) {
				return;
			}
			userMissionCompleteDao.update(request.getFinishCount().add(userMissionComplete.getInviteVolume()),
					userMissionComplete.getId());
		}
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void award(UserMissionRequest request) {
		synchronized (request.getUserId()) {
			MissionActivityDTO activity = missionActivityService.queryByType(request.getActivityType());
			UserMissionComplete userMissionComplete = queryMissionComplete(request.getUserId(),
					request.getActivityType(), activity.getDaily());
			if (activity.getAwardCreate() && userMissionComplete == null) {
				this.create(UserMissionRequest.builder().userId(request.getUserId()).activityType(activity.getType())
						.build());
				userMissionComplete = queryMissionComplete(request.getUserId(), request.getActivityType(),
						activity.getDaily());

			}
			verifyComplete(request, userMissionComplete);
			if (request.getFinishCount().add(userMissionComplete.getVolume())
					.compareTo(new BigDecimal(activity.getMinLimitNumber())) == 0) {
				userMissionComplete.setStatus(true);
			}
			userMissionComplete.setVolume(request.getFinishCount().add(userMissionComplete.getVolume()));
			userMissionCompleteDao.updateById(userMissionComplete);
			this.createActivityFlow(activity, userMissionComplete, request.getFinishCount());
		}
	}

	private void verifyComplete(UserMissionRequest request, UserMissionComplete userMissionComplete) {
		if (userMissionComplete == null)
			throw new ServerException(5001);
		if (userMissionComplete.getVolume().compareTo(userMissionComplete.getMinLimitVolume()) == 0) {
			// 当天已领取该奖励，请明天再来
			throw new ServerException(5002);
		}
		if (request.getFinishCount().compareTo(userMissionComplete.getInviteVolume()) > 0
				|| request.getFinishCount().compareTo(userMissionComplete.getMinLimitVolume()) > 0) {
			// 领取奖励大于任务奖励，无法领取
			throw new ServerException(5008);
		}
		if (request.getFinishCount().add(userMissionComplete.getVolume())
				.compareTo(userMissionComplete.getInviteVolume()) > 0
				|| request.getFinishCount().add(userMissionComplete.getVolume())
						.compareTo(userMissionComplete.getMinLimitVolume()) > 0) {
			// 领取奖励大于任务奖励，无法领取
			throw new ServerException(5008);
		}
	}

	private void buildUserMissionComplete(UserMissionRequest request, MissionActivityDTO activity) {
		UserMissionComplete userMissionComplete = new UserMissionComplete();
		userMissionComplete.setId(idWorker.nextId());
		userMissionComplete.setVolume(BigDecimal.ZERO);
		userMissionComplete.setActivityType(activity.getType());
		userMissionComplete.setInviteVolume(request.getFinishCount());
		userMissionComplete.setMinLimitVolume(new BigDecimal(activity.getMinLimitNumber()));
		userMissionComplete.setLocalDate(LocalDate.now().toString());
		userMissionComplete.setUserId(request.getUserId());
		if (activity.getType() == ActivityTypeEnum.INVITE) {
			userMissionComplete.setInviteAuditStatus(InviteAuditStatusEnum.PENDING.name());
		}
		userMissionCompleteDao.insert(userMissionComplete);
	}

	private UserMissionComplete queryMissionComplete(Long userId, ActivityTypeEnum type, Boolean daily) {
		QueryWrapper<UserMissionComplete> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq(UserMissionComplete.USER_ID, userId);
		queryWrapper.eq(UserMissionComplete.ACTIVITY_TYPE, type);
		if (daily)
			queryWrapper.eq(UserMissionComplete.LOCAL_DATE, LocalDate.now().toString());
		return userMissionCompleteDao.selectOne(queryWrapper);
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void autoAward(ActivityTypeEnum activityType, String localDate) {
		MissionActivityDTO activity = missionActivityService.queryByType(activityType);
		List<UserMissionComplete> completeList = userMissionCompleteDao
				.selectList(new QueryWrapper<UserMissionComplete>().eq(UserMissionComplete.ACTIVITY_TYPE, activityType)
						.eq(UserMissionComplete.LOCAL_DATE, localDate)
						.gt(UserMissionComplete.INVITE_VOLUME, activity.getMinLimitNumber())
						.le(UserMissionComplete.INVITE_VOLUME, activity.getMaxLimitNumber()));
		if (!CollectionUtils.isEmpty(completeList)) {
			for (UserMissionComplete complete : completeList) {
				amqpService.convertAndSend(AMQPService.AMQPPublishMode.CONFIRMS,
						RabbitMessageQueue.QUEUE_USER_MISSION_COMPLETE_STATEMENT,
						new AMQPMessage<UserMissionCompleteMessage>(new UserMissionCompleteMessage(complete.getUserId(),
								complete.getId(), complete.getInviteVolume(), activity.getAmount(),
								new BigDecimal(activity.getMinLimitNumber()))));
			}
		}
	}

	private String buildTransactionRemark(String name) {
		TransactionRemark<TradeTransactionRemark> remark = new TransactionRemark<>(TransactionRemark.RemarkEnum.TRADE,
				TradeTransactionRemark.builder().args(name).build());
		return remark.toString();
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Deprecated
	@Transactional(rollbackFor = Exception.class)
	void createActivityFlow(MissionActivityDTO activity, UserMissionComplete complete, BigDecimal finishCount) {
		List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
		switch (activity.getType()) {
		case SIGN_IN:
			transactionBeanList.add(AccountTransactionBean.builder().userId(complete.getUserId())
					.type(TransactionEnum.CREDIT_LOGIN_PROFIT).amount(activity.getAmount())
					.transactionId(activity.getId()).currency(staticConfig.getDefaultCurrency()).build());
			break;
		case INVITE:
			transactionBeanList.add(AccountTransactionBean.builder().userId(complete.getUserId())
					.type(TransactionEnum.CREDIT_INVITE_REGISTER).amount(activity.getAmount().multiply(finishCount))
					.transactionId(activity.getId()).currency(staticConfig.getDefaultCurrency()).build());
			break;
		case INVESTMENT:
			transactionBeanList.add(AccountTransactionBean.builder().userId(complete.getUserId())
					.type(TransactionEnum.CREDIT_ACTIVITY_WAGER).amount(activity.getAmount())
					.transactionId(activity.getId()).currency(staticConfig.getDefaultCurrency()).build());
			break;
		case SUNSHINE:
		case SHARECHAT:
		case MOJ:
		case DISCORD:
		case FACE_BOOK:
		case SNAPCHAT:
		case WHATS_APP:
		case YOUTUBE:
		case ZALO:
		case INSTAGRAM:
		case MESSAGE:
		case TWITTER:
		case TELEGRAM:
		case TIKTOK:
		case SUNSHINE_PROFIT:
		case SUNSHINE_RECHARGE:
		case SUNSHINE_WITHDRAWAL:
		case JOIN_TG_GROUP:
		case SUB_TG_CHANNEL:
		case SHARE_ORDER:
		case SHARE_JOB:
			transactionBeanList.add(AccountTransactionBean.builder().userId(complete.getUserId())
					.type(TransactionEnum.CREDIT_SUNSHINE).amount(activity.getAmount()).transactionId(activity.getId())
					.currency(staticConfig.getDefaultCurrency()).remark(buildTransactionRemark(activity.getType().name()))
					.build());
			break;
		}
		if (!CollectionUtils.isEmpty(transactionBeanList)) {
			accountService.transaction(complete.getUserId(), transactionBeanList);
		}
	}

	/**
	 * 邀请注册审核列表
	 *
	 * @param status
	 * @param day
	 * @param uidList
	 * @param page
	 * @param size
	 * @return
	 */
	public PageInfo<InviteTaskAuditDTO> queryList(InviteAuditStatusEnum status, LocalDate day, List<Long> uidList,
			int page, int size) {
		MissionActivityDTO activity = missionActivityService.queryByType(ActivityTypeEnum.INVITE);
		QueryWrapper<UserMissionComplete> queryWrapper = new QueryWrapper<>();
		if (status != null) {
			queryWrapper = queryWrapper.eq(UserMissionComplete.INVITE_AUDIT_STATUS, status);
		}
		if (day != null) {
			queryWrapper = queryWrapper.eq(UserMissionComplete.LOCAL_DATE, day.toString());
		}
		if (!CollectionUtils.isEmpty(uidList)) {
			queryWrapper = queryWrapper.in(UserMissionComplete.USER_ID, uidList);
		}
		queryWrapper = queryWrapper.eq(UserMissionComplete.ACTIVITY_TYPE, activity.getType())
				.gt(UserMissionComplete.INVITE_VOLUME, activity.getMaxLimitNumber())
				.orderByDesc(UserMissionComplete.GMT_CREATE);
		IPage<UserMissionComplete> iPage = userMissionCompleteDao.selectPage(new Page<>(page, size), queryWrapper);
		List<Long> uidLs = new ArrayList<>();
		PageInfo<InviteTaskAuditDTO> pageInfo = new PageInfo<>();
		if (iPage.getTotal() > 0) {
			addUidList(iPage, uidLs);
			Map<Long, String> baseMap = queryListUsername(uidLs);
			List<InviteTaskAuditDTO> auditDTOList = iPage.getRecords().stream().map(completeCount -> {
				InviteTaskAuditDTO count = new InviteTaskAuditDTO();
				count.setActivityType(completeCount.getActivityType());
				count.setUserCount(completeCount.getInviteVolume().intValue());
				count.setUserId(completeCount.getUserId());
				count.setStatus(InviteAuditStatusEnum.valueOf(completeCount.getInviteAuditStatus()));
				count.setDay(completeCount.getLocalDate());
				count.setId(completeCount.getId());
				count.setUsername(baseMap.get(completeCount.getUserId()));
				return count;
			}).collect(Collectors.toList());
			pageInfo.setRecords(auditDTOList);
			pageInfo.setPage((int) iPage.getPages());
			pageInfo.setSize((int) iPage.getSize());
			pageInfo.setTotal(iPage.getTotal());
		}
		return pageInfo;
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void audit(InviteTaskAuditRequest request) {
		UserMissionComplete complete = userMissionCompleteDao.selectById(request.getId());
		if (complete != null
				&& InviteAuditStatusEnum.valueOf(complete.getInviteAuditStatus()) == InviteAuditStatusEnum.PENDING) {
			MissionActivityDTO activity = missionActivityService.queryByType(complete.getActivityType());
			complete.setInviteAuditStatus(request.getStatus().name());
			userMissionCompleteDao.updateById(complete);
			if (request.getStatus() == InviteAuditStatusEnum.PASS) {
				BigDecimal number = complete.getInviteVolume().subtract(new BigDecimal(activity.getMinLimitNumber()));
				List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
				transactionBeanList.add(AccountTransactionBean.builder().userId(complete.getUserId())
						.type(TransactionEnum.CREDIT_INVITE_REGISTER).amount(activity.getAmount().multiply(number))
						.transactionId(complete.getId()).currency(staticConfig.getDefaultCurrency()).build());
				accountService.transaction(complete.getUserId(), transactionBeanList);
			}
			return;
		}
		throw new ServerException(5010);
	}

	private void addUidList(IPage<UserMissionComplete> iPage, List<Long> uidList) {
		for (UserMissionComplete record : iPage.getRecords()) {
			if (!uidList.contains(record.getUserId())) {
				uidList.add(record.getUserId());
			}
		}
	}

	private Map<Long, String> queryListUsername(List<Long> uidList) {
		Map<Long, String> baseMap = new HashMap<>();
		List<User> userList = userDao.selectList(new QueryWrapper<User>().in(User.ID, uidList));
		if (!CollectionUtils.isEmpty(userList)) {
			for (User user : userList) {
				baseMap.put(user.getId(), user.getUsername());
			}
		}
		return baseMap;
	}
}
