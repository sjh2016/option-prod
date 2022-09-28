package com.waben.option.core.amqp.user;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.waben.option.common.amqp.BaseAMPQConsumer;
import com.waben.option.common.amqp.message.UserBerealMessage;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.core.service.activity.ActivityService;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.entity.user.UserSta;
import com.waben.option.data.repository.user.UserDao;
import com.waben.option.data.repository.user.UserStaDao;

@Component
@RabbitListener(containerFactory = "rabbitListenerContainerFactory", admin = "amqpAdmin", queues = RabbitMessageQueue.QUEUE_USER_BEREAL)
public class UserBerealConsumer extends BaseAMPQConsumer<UserBerealMessage> {

	@Resource
	private UserDao userDao;

	@Resource
	private UserStaDao userStaDao;

	@Resource
	private ActivityService activityService;

	@Resource
	private StaticConfig staticConfig;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void handle(UserBerealMessage message) {
		// 更新用户为真实用户
		User user = userDao.selectById(message.getUserId());
		if (user.getIsReal() != null && user.getIsReal()) {
			return;
		}
		user.setIsReal(true);
		userDao.updateById(user);
		UserSta userSta = userStaDao.selectById(message.getUserId());
		userSta.setIsReal(true);
		userStaDao.updateById(userSta);
		// 给上级用户发放邀请奖励
		if (user.getParentId() != null && user.getParentId().longValue() > 0) {
			UserSta parentSta = userStaDao.selectById(user.getParentId());
			if (parentSta != null) {
				Integer inviteRealCount = parentSta.getInviteRealCount();
				if (inviteRealCount == null) {
					inviteRealCount = 0;
				}
				parentSta.setInviteRealCount(inviteRealCount + 1);
				userStaDao.updateById(parentSta);
			}
			if (!staticConfig.isContract()) {
				activityService.realInviteReceive(user.getId(), user.getParentId());
			}
		}
	}

}
