package com.waben.option.core.controller.user;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.query.UserPageQuery;
import com.waben.option.common.model.request.common.IdRequest;
import com.waben.option.common.model.request.user.GenerateSubordinateRequest;
import com.waben.option.common.model.request.user.RegisterUserRequest;
import com.waben.option.common.model.request.user.UpdatePassword1Request;
import com.waben.option.common.model.request.user.UpdatePassword2Request;
import com.waben.option.common.model.request.user.UpdateUserBasicRequest;
import com.waben.option.common.model.request.user.UserLoggerRequest;
import com.waben.option.common.model.request.user.UserSubordinateRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.user.UserService;
import com.waben.option.core.service.user.logger.UserLoggerService;

@RestController
@RequestMapping("user")
public class UserController extends AbstractBaseController {

    @Resource
    private UserService userService;

    @Resource
    private UserLoggerService userLoggerService;


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody RegisterUserRequest request) {
        return ok(userService.register(request));
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<?> login(String username, String password, String ip, String code) {
        return ok(userService.login(username, password, ip, code));
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity<?> logout(Long currentUserId) {
        userService.logout(currentUserId);
        return ok();
    }

    @RequestMapping(value = "/storeToken", method = RequestMethod.GET)
    public ResponseEntity<?> storeToken(Long userId, String token) {
        userService.storeToken(userId, token);
        return ok();
    }

    @RequestMapping(value = "/cleanToken", method = RequestMethod.GET)
    public ResponseEntity<?> cleanToken(Long userId) {
        userService.cleanToken(userId);
        return ok();
    }

    @RequestMapping(value = "/verify/username", method = RequestMethod.GET)
    public ResponseEntity<?> verifyUsername(String username) {
        return ok(userService.verifyUsername(username));
    }

    @RequestMapping(value = "/update/password/login1", method = RequestMethod.POST)
    public ResponseEntity<?> updateLoginPassword(@RequestBody UpdatePassword1Request request) {
        userService.updateLoginPassword1(request.getUserId(), request.getOldPassword(), request.getNewPassword());
        return ok();
    }

    @RequestMapping(value = "/update/password/login2", method = RequestMethod.POST)
    public ResponseEntity<?> updateLoginPassword(@RequestBody UpdatePassword2Request request) {
        userService.updateLoginPassword2(request);
        return ok();
    }

    @RequestMapping(value = "/reset/login/password", method = RequestMethod.GET)
    public ResponseEntity<?> resetLoginPassword(@RequestParam("userId") Long userId,
                                                @RequestParam("password") String password) {
        userService.resetLoginPassword(userId, password);
        return ok();
    }

    @RequestMapping(value = "/update/password/fund1", method = RequestMethod.POST)
    public ResponseEntity<?> updateFundPassword(@RequestBody UpdatePassword1Request request) {
        userService.updateFundPassword1(request.getUserId(), request.getOldPassword(), request.getNewPassword());
        return ok();
    }

    @RequestMapping(value = "/update/password/fund2", method = RequestMethod.POST)
    public ResponseEntity<?> updateFundPassword(@RequestBody UpdatePassword2Request request) {
        userService.updateFundPassword2(request);
        return ok();
    }

    @RequestMapping(value = "/update/updateUserBasic", method = RequestMethod.POST)
    public ResponseEntity<?> updateUserBasic(@RequestBody UpdateUserBasicRequest request) {
        userService.updateUserBasic(request);
        return ok();
    }

    @RequestMapping(value = "/queryUserPage", method = RequestMethod.POST)
    public ResponseEntity<?> queryUserPage(@RequestBody UserPageQuery userQuery) {
        return ok(userService.queryUserPage(userQuery));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/query")
    public ResponseEntity<?> queryUser(@RequestParam("id") Long id) {
        return ok(userService.queryUser(id));
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/queryByUsername")
    public ResponseEntity<?> queryByUsername(@RequestParam("username") String username) {
        return ok(userService.queryByUsername(username));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/uidList")
    public ResponseEntity<?> queryUserList(@RequestParam("uidList") List<Long> uidList) {
        return ok(userService.queryUserList(uidList));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/query/logger")
    public ResponseEntity<?> queryUserLogger(@RequestBody UserLoggerRequest request) {
        return ok(userLoggerService.queryUserLoggerPage(request));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/query/loggerAction")
    public ResponseEntity<?> queryLoggerAction(@RequestParam(value = "cmdList", required = false) List<String> cmdList, @RequestParam(value = "platform", required = false) String platform) {
        return ok(userLoggerService.queryLoggerAction(cmdList, platform));
    }

    @RequestMapping(value = "/queryIncome", method = RequestMethod.GET)
    public ResponseEntity<?> queryIncome(Long userId) {
        return ok(userService.queryIncome(userId));
    }

    @RequestMapping(value = "/queryRank", method = RequestMethod.GET)
    public ResponseEntity<?> queryRank(@RequestParam(value = "userId", required = false) Long userId,
                                       @RequestParam(value = "rankType", required = false) String rankType) {
        return ok(userService.queryRank(userId, rankType));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/queryUserTreeNode")
    public ResponseEntity<?> queryUserTreeNode(@RequestParam("userId") Long userId) {
        return ok(userService.queryUserTreeNodeRebuild(userId));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/invitePeopleByUsers")
    public ResponseEntity<?> invitePeopleByUsers(@RequestParam("symbol") String symbol) {
        return ok(userService.invitePeopleByUsers(symbol));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/queryMobilePhone")
    public ResponseEntity<?> queryMobilePhone(@RequestParam("mobilePhone") String mobilePhone) {
        return ok(userService.queryMobilePhone(mobilePhone));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/userRatioDivide")
    public ResponseEntity<?> userRatioDivide(@RequestParam("amount") BigDecimal amount, @RequestParam("userId") Long userId) {
        userService.userRatioDivide(amount, userId);
        return ok();
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/subordinatePage")
    public ResponseEntity<?> subordinatePage(@RequestBody UserSubordinateRequest req) {
        return ok(userService.subordinatePage(req));
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/black")
	public ResponseEntity<?> black(@RequestBody IdRequest req) {
    	userService.black(req.getId());
		return ok();
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/unblack")
	public ResponseEntity<?> unblack(@RequestBody IdRequest req) {
		userService.unblack(req.getId());
		return ok();
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/generateSubordinate")
	public ResponseEntity<?> generateSubordinate(@RequestBody GenerateSubordinateRequest req) {
		userService.generateSubordinate(req);
		return ok();
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/updateStarLevel")
	public ResponseEntity<?> updateStarLevel(@RequestParam(value = "userId", required = true) Long userId, @RequestParam(value = "starLevel", required = true) Integer starLevel) {
		userService.updateStarLevel(userId, starLevel);
		return ok();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/sta")
    public ResponseEntity<?> sta(@RequestParam("userId") Long userId) {
        return ok(userService.sta(userId));
    }

}
