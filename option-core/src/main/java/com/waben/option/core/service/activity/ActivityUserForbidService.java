package com.waben.option.core.service.activity;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.data.entity.activity.ActivityUserForbid;
import com.waben.option.data.repository.activity.ActivityUserForbidDao;

@Service
public class ActivityUserForbidService {

	@Resource
	private ActivityUserForbidDao forbidDao;

	public boolean isForbid(Long userId, ActivityTypeEnum activityType) {
		QueryWrapper<ActivityUserForbid> query = new QueryWrapper<>();
		query.eq(ActivityUserForbid.USER_ID, userId);
		query.eq(ActivityUserForbid.ACTIVITY_TYPE, activityType);
		Integer check = forbidDao.selectCount(query);
		if (check != null && check.intValue() > 0) {
			return true;
		} else {
			return false;
		}
	}

}
