package com.waben.option.core.service.activity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.exception.BusinessErrorConstants;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.order.OrderAPI;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.data.entity.activity.ActivityJoinRecord;
import com.waben.option.data.repository.activity.ActivityJoinRecordDao;

@Service
public class ActivityJoinRecordService {

	@Resource
	private IdWorker idWorker;

	@Resource
	private ActivityJoinRecordDao activityJoinRecordDao;

	@Resource
	private OrderAPI orderAPI;

	public void join(Long userId, ActivityTypeEnum activityType) {
		if (activityType == ActivityTypeEnum.TESLA_GIVE) {
			// 检查是否已经参与过
			QueryWrapper<ActivityJoinRecord> query = new QueryWrapper<>();
			query.eq(ActivityJoinRecord.USER_ID, userId);
			query.eq(ActivityJoinRecord.ACTIVITY_TYPE, activityType);
			List<ActivityJoinRecord> checkList = activityJoinRecordDao.selectList(query);
			if (checkList != null && checkList.size() > 0) {
				throw new ServerException(BusinessErrorConstants.ERROR_ACTIVITY_JOIN_ALREADY);
			}
			// 判断是否满足参与条件
			BigDecimal totalAmount = orderAPI.userPlaceCount(userId);
			if (totalAmount.compareTo(new BigDecimal(50000000)) < 0) {
				throw new ServerException(BusinessErrorConstants.ERROR_ACTIVITY_JOIN_NOT_REACH);
			}
			// 增加参与记录
			ActivityJoinRecord record = new ActivityJoinRecord();
			record.setId(idWorker.nextId());
			record.setUserId(userId);
			record.setActivityType(activityType);
			record.setJoinTime(LocalDateTime.now());
			activityJoinRecordDao.insert(record);
		}
	}

	public boolean hasJoin(Long userId, ActivityTypeEnum activityType) {
		QueryWrapper<ActivityJoinRecord> query = new QueryWrapper<>();
		query.eq(ActivityJoinRecord.USER_ID, userId);
		query.eq(ActivityJoinRecord.ACTIVITY_TYPE, activityType);
		List<ActivityJoinRecord> checkList = activityJoinRecordDao.selectList(query);
		if (checkList != null && checkList.size() > 0) {
			return true;
		}
		return false;
	}

}
