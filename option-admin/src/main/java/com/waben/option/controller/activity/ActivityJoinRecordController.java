package com.waben.option.controller.activity;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.exception.BusinessErrorConstants;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfacesadmin.activity.AdminActivityJoinRecordAPI;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.web.controller.AbstractBaseController;

@RestController
@RequestMapping("/activity")
public class ActivityJoinRecordController extends AbstractBaseController {

	@Resource
	private AdminActivityJoinRecordAPI adminActivityJoinRecordAPI;

	@RequestMapping(value = "/tesla/join", method = RequestMethod.POST)
	public ResponseEntity<?> teslaJoin() {
		adminActivityJoinRecordAPI.join(getCurrentUserId(), ActivityTypeEnum.TESLA_GIVE);
		throw new ServerException(BusinessErrorConstants.ERROR_ACTIVITY_JOIN_SUCCESS);
	}

}
