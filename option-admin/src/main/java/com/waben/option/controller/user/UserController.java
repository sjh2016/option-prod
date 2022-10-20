package com.waben.option.controller.user;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.waben.option.common.interfaces.user.UserAPI;
import com.waben.option.common.model.dto.user.QueryUserInvitePeopleDTO;
import com.waben.option.common.model.dto.user.UserInvitePeopleDTO;
import com.waben.option.mode.vo.UserVO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.exception.BusinessErrorConstants;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfacesadmin.user.AdminUserAPI;
import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.common.model.query.UserPageQuery;
import com.waben.option.common.model.request.common.IdRequest;
import com.waben.option.common.model.request.user.UserSubordinateRequest;
import com.waben.option.common.util.AesEncryptUtil;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.mode.request.ClientRegisterUserRequest;
import com.waben.option.mode.request.ClientUpdatePassword1Request;
import com.waben.option.mode.request.ClientUpdatePassword2Request;
import com.waben.option.mode.request.ClientUpdateUserBasicRequest;
import com.waben.option.service.user.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Logger;

@Slf4j
@Api(tags = { "用户信息" })
@Validated
@RestController
@RequestMapping("/user")
public class UserController extends AbstractBaseController {

	@Resource
	private UserService userService;

	@Resource
	private AdminUserAPI adminUserAPI;

	@Resource
	private UserAPI userAPI;

	@ApiOperation(value = "注册")
	@Validated
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> register(@RequestBody @Valid ClientRegisterUserRequest request) {
		if (request.getIp() == null) {
			request.setIp(getUserIp());
		}
		if (request.getUsername().startsWith("0")) {
			request.setUsername(request.getUsername().substring(1));
		}
		return ok(userService.register(request));
	}

	@ApiOperation(value = "注册")
	@Validated
	@RequestMapping(value = "/register/encrypt", method = RequestMethod.POST)
	public ResponseEntity<?> registerEncrypt(@RequestBody String encryptJson) {
		// 解密数据
		String json = null;
		try {
			if (encryptJson != null) {
				encryptJson = encryptJson.trim();
			}
			json = AesEncryptUtil.decrypt(encryptJson);
		} catch (Exception ex) {
			log.error("register decrypt failed:" + encryptJson);
			throw new ServerException(BusinessErrorConstants.ERROR_PARAM_FORMAT);
		}
		// 执行业务
		ClientRegisterUserRequest request = JacksonUtil.decode(json, ClientRegisterUserRequest.class);
		if (request.getIp() == null) {
			request.setIp(getUserIp());
		}
		if (request.getUsername().startsWith("0")) {
			request.setUsername(request.getUsername().substring(1));
		}
		return ok(userService.register(request));
	}

	@ApiOperation(value = "根据旧密码修改登录密码")
	@RequestMapping(value = "/update/password/login1", method = RequestMethod.POST)
	public ResponseEntity<?> updateLoginPassword(@RequestBody ClientUpdatePassword1Request request) {
		userService.updateLoginPassword(request);
		return ok();
	}

	@ApiOperation(value = "根据验证码修改登录密码")
	@RequestMapping(value = "/update/password/login2", method = RequestMethod.POST)
	public ResponseEntity<?> updateLoginPassword(@RequestBody ClientUpdatePassword2Request request) {
		if (request.getUsername().startsWith("0")) {
			request.setUsername(request.getUsername().substring(1));
		}
		userService.updateLoginPassword(request);
		return ok();
	}

	@ApiOperation(value = "根据旧密码修改支付密码")
	@RequestMapping(value = "/update/password/fund1", method = RequestMethod.POST)
	public ResponseEntity<?> updateFundPassword(@RequestBody ClientUpdatePassword1Request request) {
		userService.updateFundPassword(request);
		return ok();
	}

	@ApiOperation(value = "根据验证码修改支付密码", hidden = true)
	@RequestMapping(value = "/update/password/fund2", method = RequestMethod.POST)
	public ResponseEntity<?> updateFundPassword(@RequestBody ClientUpdatePassword2Request request) {
		userService.updateFundPassword(request);
		return ok();
	}

	@ApiOperation(value = "查询个人用户")
	@RequestMapping(value = "/queryById", method = RequestMethod.GET)
	public ResponseEntity<?> queryById() {
		return ok(userService.queryUser(getCurrentUserId()));
	}

	@RequestMapping(value = "/queryUserPage", method = RequestMethod.POST)
	public ResponseEntity<?> queryUserPage(@RequestBody UserPageQuery userQuery) {
		userQuery.setAuthorityType(AuthorityEnum.CLIENT);
		return ok(userService.queryUserPage(userQuery));
	}

	@ApiOperation(value = "校验用户是否存在")
	@RequestMapping(value = "/verify/username", method = RequestMethod.GET)
	public ResponseEntity<?> verifyUsername(@RequestParam("username") String username) {
		if (username.startsWith("0"))
			username = username.substring(1);
		return ok(userService.verifyUsername(username));
	}

	@ApiOperation(value = "更新用户基本信息")
	@RequestMapping(value = "/update/updateUserBasic", method = RequestMethod.POST)
	public ResponseEntity<?> updateUserBasic(@RequestBody ClientUpdateUserBasicRequest request) {
		request.setUserId(getCurrentUserId());
		userService.updateUserBasic(request);
		return ok();
	}

	@ApiOperation("获取用户收益")
	@RequestMapping(value = "/queryIncome", method = RequestMethod.GET)
	public ResponseEntity<?> queryIncome() {
		return ok(userService.queryIncome(getCurrentUserId()));
	}

	@Deprecated
	@ApiOperation("获取我的排名")
	@RequestMapping(value = "/queryRank", method = RequestMethod.GET)
	public ResponseEntity<?> queryRank(@RequestParam(value = "rankType", required = false) String rankType) {
		return ok(userService.queryRank(getCurrentUserId(), rankType));
	}

	@RequestMapping(value = "/queryUserTreeNode", method = RequestMethod.GET)
	public ResponseEntity<?> queryUserTreeNode() {
		return ok(userService.queryUserTreeNode(getCurrentUserId()));
	}


	@ApiOperation("获取分销用户列表信息目前到4级")
	@RequestMapping(value = "/queryUserTreeNode/new", method = RequestMethod.GET)
	public ResponseEntity<?> queryUserTreeNodeNew(@RequestParam("userId") Long  userId,
												  @RequestParam("level") int level,
												  @RequestParam(value = "childUserId",required = false) Long childUserId,
												  @RequestParam(value = "page",required = false) int page,
												  @RequestParam(value = "size",required = false) int size) {
		return ok(userService.queryUserTreeNodeNew(userId, level,childUserId,page,size));
	}


	@RequestMapping(value = "/reset/login/password", method = RequestMethod.GET)
	public ResponseEntity<?> resetLoginPassword(@RequestParam("userId") Long userId,
			@RequestParam("password") String password) {
		userService.resetLoginPassword(userId, password);
		return ok();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/subordinatePage")
	public ResponseEntity<?> subordinatePage(@RequestBody UserSubordinateRequest req) {
		return ok(adminUserAPI.subordinatePage(req));
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/black")
	public ResponseEntity<?> black(@RequestBody IdRequest req) {
		adminUserAPI.black(req);
		return ok();
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/unblack")
	public ResponseEntity<?> unblack(@RequestBody IdRequest req) {
		adminUserAPI.unblack(req);
		return ok();
	}

	@ApiOperation(value = "获取用户邀请人信息")
	@RequestMapping(value = "/queryUserInvitePeople", method = RequestMethod.POST)
	public ResponseEntity<?> queryUserInvitePeople(@RequestBody QueryUserInvitePeopleDTO queryUserInvitePeopleDTO) {
		log.info("queryUserInvitePeople 开始 queryUserInvitePeopleDTO={}",queryUserInvitePeopleDTO);
		UserInvitePeopleDTO userInvitePeopleDTO = userAPI.queryUserInvitePeople(queryUserInvitePeopleDTO);
		log.info("queryUserInvitePeople 结束 userInvitePeopleDTO={}",userInvitePeopleDTO);
		return ok(userInvitePeopleDTO);
	}


	@RequestMapping(value = "/queryCount", method = RequestMethod.GET)
	public ResponseEntity<?> queryCount(@RequestParam(value = "topId", required = false) String topId) {
		UserPageQuery userQuery = new UserPageQuery();
		userQuery.setAuthorityType(AuthorityEnum.CLIENT);
		LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
		LocalDateTime endDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
		userQuery.setRegisterStart(startDateTime);
		userQuery.setRegisterEnd(endDateTime);
		userQuery.setTopId(topId);
		return ok(userService.queryUserCount(userQuery));
	}




}
