package com.waben.option.common.interfaces.user;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.user.*;
import com.waben.option.common.model.dto.user.logger.LoggerCommandDTO;
import com.waben.option.common.model.dto.user.logger.UserLoggerDTO;
import com.waben.option.common.model.query.UserPageQuery;
import com.waben.option.common.model.request.common.IdRequest;
import com.waben.option.common.model.request.user.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(value = "core-server", contextId = "UserAPI", qualifier = "userAPI")
public interface UserAPI extends BaseAPI {

    @RequestMapping(method = RequestMethod.GET, value = "/user/query")
    public Response<UserDTO> _queryUser(@RequestParam("id") Long id);
    
    @RequestMapping(method = RequestMethod.GET, value = "/user/queryByUsername")
    public Response<UserDTO> _queryUserByUsername(@RequestParam("username") String username);

    @RequestMapping(method = RequestMethod.GET, value = "/user/uidList")
    public Response<List<UserDTO>> _queryUserList(@RequestParam("uidList") List<Long> uidList);

    @RequestMapping(method = RequestMethod.POST, value = "/user/register")
    public Response<UserDTO> _register(@RequestBody RegisterUserRequest request);

    @RequestMapping(method = RequestMethod.POST, value = "/user/update/password/login1")
    public Response<Void> _updateLoginPassword(@RequestBody UpdatePassword1Request request);

    @RequestMapping(method = RequestMethod.POST, value = "/user/update/password/login2")
    public Response<Void> _updateLoginPassword(@RequestBody UpdatePassword2Request request);

    @RequestMapping(method = RequestMethod.POST, value = "/user/update/password/fund1")
    public Response<Void> _updateFundPassword(@RequestBody UpdatePassword1Request request);

    @RequestMapping(method = RequestMethod.POST, value = "/user/update/password/fund2")
    public Response<Void> _updateFundPassword(@RequestBody UpdatePassword2Request request);

    @RequestMapping(method = RequestMethod.GET, value = "/user/login")
    public Response<UserDTO> _login(@RequestParam("username") String username,
                                    @RequestParam("password") String password, @RequestParam("ip") String ip,
                                    @RequestParam("code") String code);

    @RequestMapping(method = RequestMethod.GET, value = "/user/logout")
    public Response<Void> _logout(@RequestParam("currentUserId") Long currentUserId);

    @RequestMapping(value = "/user/storeToken", method = RequestMethod.GET)
    public Response<Void> _storeToken(@RequestParam("userId") Long userId, @RequestParam("token") String token);

    @RequestMapping(value = "/user/cleanToken", method = RequestMethod.GET)
    public Response<Void> _cleanToken(@RequestParam("userId") Long userId);

    @RequestMapping(method = RequestMethod.GET, value = "/user/verify/username")
    public Response<Boolean> _verifyUsername(@RequestParam("username") String username);

    @RequestMapping(method = RequestMethod.POST, value = "/user/queryUserPage")
    public Response<PageInfo<UserDTO>> _queryUserPage(@RequestBody UserPageQuery userQuery);

    @RequestMapping(method = RequestMethod.POST, value = "/user/update/updateUserBasic")
    public Response<Void> _updateUserBasic(@RequestBody UpdateUserBasicRequest request);

    @RequestMapping(method = RequestMethod.POST, value = "/user/query/logger")
    public Response<PageInfo<UserLoggerDTO>> _queryUserLogger(@RequestBody UserLoggerRequest request);

    @RequestMapping(method = RequestMethod.GET, value = "/user/query/loggerAction")
    public Response<List<LoggerCommandDTO>> _queryLoggerAction(@RequestParam(value = "cmdList", required = false) List<String> cmdList,
                                                               @RequestParam(value = "platform", required = false) String platform);

    @RequestMapping(method = RequestMethod.GET, value = "/user/queryIncome")
    public Response<UserIncomeDTO> _queryIncomet(@RequestParam(value = "userId", required = false) Long userId);

    @RequestMapping(method = RequestMethod.GET, value = "/user/queryRank")
    public Response<Integer> _queryRank(@RequestParam(value = "userId", required = false) Long userId,
                                        @RequestParam(value = "rankType", required = false) String rankType);

    @RequestMapping(method = RequestMethod.GET, value = "/user/queryUserTreeNode")
    public Response<List<UserInviteTreeDTO>> _queryUserTreeNode(@RequestParam(value = "userId", required = false) Long userId, @RequestParam(value = "level", required = false) int level);

    @RequestMapping(method = RequestMethod.GET, value = "/user/invitePeopleByUsers")
    public Response<Integer> _invitePeopleByUsers(@RequestParam("symbol") String symbol);

    @RequestMapping(method = RequestMethod.GET, value = "/user/queryMobilePhone")
    public Response<Long> _queryMobilePhone(@RequestParam("mobilePhone") String mobilePhone);

    @RequestMapping(method = RequestMethod.GET, value = "/user/reset/login/password")
    public Response<Void> _resetLoginPassword(@RequestParam("userId") Long userId,
                                              @RequestParam("password") String password);

    @RequestMapping(method = RequestMethod.GET, value = "/user/userRatioDivide")
    public Response<Void> _userRatioDivide(@RequestParam("amount") BigDecimal amount, @RequestParam("userId") Long userId);
    
    @RequestMapping(method = RequestMethod.POST, value = "/user/subordinatePage")
    public Response<PageInfo<UserDTO>> _subordinatePage(@RequestBody UserSubordinateRequest req);
    
    @RequestMapping(method = RequestMethod.POST, value = "/user/black")
	public Response<Void> _black(@RequestBody IdRequest req);
	
	@RequestMapping(method = RequestMethod.POST, value = "/user/unblack")
	public Response<Void> _unblack(@RequestBody IdRequest req);
	
	@RequestMapping(method = RequestMethod.GET, value = "/user/sta")
    public Response<UserStaDTO> _sta(@RequestParam(value = "userId") Long userId,
                                     @RequestParam(value = "level") int level);

    @RequestMapping(method = RequestMethod.GET, value = "/user/newsta")
    public Response<UserCountDTO> _newsta(@RequestParam(value = "userId") Long userId);
	
    public default Void userRatioDivide(BigDecimal amount, Long userId) {
        return getResponseData(_userRatioDivide(amount, userId));
    }

    public default Void resetLoginPassword(Long userId, String password) {
        return getResponseData(_resetLoginPassword(userId, password));
    }

    public default Long queryMobilePhone(String mobilePhone) {
        return getResponseData(_queryMobilePhone(mobilePhone));
    }

    public default Integer invitePeopleByUsers(String symbol) {
        return getResponseData(_invitePeopleByUsers(symbol));
    }

    /**
     * 查询用户详情
     *
     * @param id 用户id
     * @return
     */
    public default UserDTO queryUser(Long id) {
        return getResponseData(_queryUser(id));
    }
    
    public default UserDTO queryUserByUsername(String username) {
        return getResponseData(_queryUserByUsername(username));
    }
    
    public default List<UserDTO> queryUserList(List<Long> uidList) {
        return getResponseData(_queryUserList(uidList));
    }

    /**
     * 注册用户
     *
     * @param request
     * @return
     */
    public default UserDTO register(RegisterUserRequest request) {
        return getResponseData(_register(request));
    }

    /**
     * 登陆
     *
     * @param username 用户名
     * @param password 密码
     * @param ip       ip地址
     * @return
     */
    public default UserDTO login(String username, String password, String ip, String code) {
        return getResponseData(_login(username, password, ip, code));
    }

    /**
     * 登出
     */
    public default void logout(Long currentUserId) {
        getResponseData(_logout(currentUserId));
    }

    /**
     * 根据旧密码更新登陆密码
     *
     * @param request
     */
    public default void updateLoginPassword(UpdatePassword1Request request) {
        getResponseData(_updateLoginPassword(request));
    }

    /**
     * 根据验证码更新登陆密码
     *
     * @param request
     */
    public default void updateLoginPassword(@RequestBody UpdatePassword2Request request) {
        getResponseData(_updateLoginPassword(request));
    }

    /**
     * 根据旧密码更新资金密码
     *
     * @param request
     */
    public default void updateFundPassword(@RequestBody UpdatePassword1Request request) {
        getResponseData(_updateFundPassword(request));
    }

    /**
     * 根据验证码更新资金密码
     *
     * @param request
     */
    public default void updateFundPassword(@RequestBody UpdatePassword2Request request) {
        getResponseData(_updateFundPassword(request));
    }

    /**
     * 存储token
     *
     * @param userId
     * @param token
     */
    public default void storeToken(Long userId, String token) {
        getResponseData(_storeToken(userId, token));
    }

    /**
     * 清空token
     *
     * @param userId
     */
    public default void cleanToken(Long userId) {
        getResponseData(_cleanToken(userId));
    }

    /**
     * 分页查询用户列表
     * <p>enable=false时，为黑名单用户<p/>
     *
     * @param userQuery
     * @return
     */
    public default PageInfo<UserDTO> queryUserPage(UserPageQuery userQuery) {
        return getResponseData(_queryUserPage(userQuery));
    }

    /**
     * 查询用户名是否存在
     *
     * @param username 用户名
     * @return true 不存在 false 存在
     */
    public default Boolean verifyUsername(String username) {
        return getResponseData(_verifyUsername(username));
    }

    /**
     * 更新用户基本信息
     *
     * @param request
     * @return
     */
    public default Void updateUserBasic(UpdateUserBasicRequest request) {
        return getResponseData(_updateUserBasic(request));
    }


    public default PageInfo<UserLoggerDTO> queryUserLogger(UserLoggerRequest request) {
        return getResponseData(_queryUserLogger(request));
    }

    public default List<LoggerCommandDTO> queryLoggerAction(List<String> cmdList, String platform) {
        return getResponseData(_queryLoggerAction(cmdList, platform));
    }

    public default UserIncomeDTO queryIncome(Long userId) {
        return getResponseData(_queryIncomet(userId));
    }

    public default Integer queryRank(Long userId, String rankType) {
        return getResponseData(_queryRank(userId, rankType));

    }

    public default List<UserInviteTreeDTO> queryUserTreeNode(Long userId, int level) {
        return getResponseData(_queryUserTreeNode(userId, level));
    }
    
    public default PageInfo<UserDTO> subordinatePage(UserSubordinateRequest req) {
    	return getResponseData(_subordinatePage(req));
    }
    
    public default void black(IdRequest req) {
    	getResponseData(_black(req));
    }

    public default void unblack(IdRequest req) {
    	getResponseData(_unblack(req));
    }

	public default UserStaDTO sta(Long userId, int level) {
		return getResponseData(_sta(userId, level));
	}

    public default UserCountDTO newsta(Long userId) {
        return getResponseData(_newsta(userId));
    }



    @RequestMapping(method = RequestMethod.POST, value = "/user/queryUserInvitePeople")
    public Response<UserInvitePeopleDTO> _queryUserInvitePeople(@RequestBody QueryUserInvitePeopleDTO req);

    public default UserInvitePeopleDTO queryUserInvitePeople(QueryUserInvitePeopleDTO req) {
        return getResponseData(_queryUserInvitePeople(req));
    }
}
