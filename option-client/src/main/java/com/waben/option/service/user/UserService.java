package com.waben.option.service.user;

import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.order.OrderAPI;
import com.waben.option.common.interfaces.user.UserAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.order.OrderUserStaDTO;
import com.waben.option.common.model.dto.user.*;
import com.waben.option.common.model.enums.PlatformEnum;
import com.waben.option.common.model.query.UserPageQuery;
import com.waben.option.common.model.request.user.RegisterUserRequest;
import com.waben.option.common.model.request.user.UpdatePassword1Request;
import com.waben.option.common.model.request.user.UpdatePassword2Request;
import com.waben.option.common.model.request.user.UpdateUserBasicRequest;
import com.waben.option.common.service.JwtService;
import com.waben.option.common.util.CloneUtils;
import com.waben.option.mode.request.ClientRegisterUserRequest;
import com.waben.option.mode.request.ClientUpdatePassword1Request;
import com.waben.option.mode.request.ClientUpdatePassword2Request;
import com.waben.option.mode.request.ClientUpdateUserBasicRequest;
import com.waben.option.mode.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserService {

    @Resource
    private UserAPI userAPI;

    @Resource
    private OrderAPI orderAPI;

    @Resource
    private JwtService jwtService;

    @Resource
    private ModelMapper modelMapper;

    public UserVO register(ClientRegisterUserRequest clientRequest) {
        RegisterUserRequest request = modelMapper.map(clientRequest, RegisterUserRequest.class);
        UserDTO userDTO = userAPI.register(request);
        return modelMapper.map(userDTO, UserVO.class);
    }

    public Map<String, Object> registerWithToken(ClientRegisterUserRequest clientRequest) {
        RegisterUserRequest request = modelMapper.map(clientRequest, RegisterUserRequest.class);
        UserDTO userDTO = userAPI.register(request);
        UserVO userVO = modelMapper.map(userDTO, UserVO.class);
        userVO.setLoginPassword(null);
        String newToken = jwtService.sign(PlatformEnum.H5.name(), userVO.getId(), userVO.getAuthorityType().name());
        userAPI.cleanToken(userVO.getId());
        userAPI.storeToken(userVO.getId(), newToken);
        Map<String, Object> map = new HashMap<>();
        map.put("token", newToken);
        map.put("user", userVO);
        return map;
    }

    public void updateLoginPassword(ClientUpdatePassword1Request clientRequest) {
        UpdatePassword1Request request = modelMapper.map(clientRequest, UpdatePassword1Request.class);
        userAPI.updateLoginPassword(request);
    }

    public void updateLoginPassword(ClientUpdatePassword2Request clientRequest) {
        UpdatePassword2Request request = modelMapper.map(clientRequest, UpdatePassword2Request.class);
        if (StringUtils.isBlank(request.getVerifyCode())) {
            throw new ServerException(1026);
        }
        if (StringUtils.isBlank(request.getNewPassword())) {
            throw new ServerException(1025);
        }
        userAPI.updateLoginPassword(request);
    }

    public void updateFundPassword(ClientUpdatePassword1Request clientRequest) {
        UpdatePassword1Request request = modelMapper.map(clientRequest, UpdatePassword1Request.class);
        if (StringUtils.isBlank(request.getNewPassword())) {
            throw new ServerException(1025);
        }
        userAPI.updateFundPassword(request);
    }

    public void updateFundPassword(ClientUpdatePassword2Request clientRequest) {
        UpdatePassword2Request request = modelMapper.map(clientRequest, UpdatePassword2Request.class);
        if (StringUtils.isBlank(request.getVerifyCode())) {
            throw new ServerException(1026);
        }
        if (StringUtils.isBlank(request.getNewPassword())) {
            throw new ServerException(1025);
        }
        userAPI.updateFundPassword(request);
    }

    public UserVO queryUser(Long userId) {
        UserDTO user = userAPI.queryUser(userId);
        return modelMapper.map(user, UserVO.class);
    }

    public PageInfo<UserVO> queryUserPage(UserPageQuery userQuery) {
        PageInfo<UserDTO> pageInfo = userAPI.queryUserPage(userQuery);
        return CloneUtils.copy(pageInfo, UserVO.class);
    }

    public Boolean verifyUsername(String username) {
        return userAPI.verifyUsername(username);
    }

    public void updateUserBasic(ClientUpdateUserBasicRequest clientRequest) {
        UpdateUserBasicRequest request = modelMapper.map(clientRequest, UpdateUserBasicRequest.class);
        userAPI.updateUserBasic(request);
    }

    public UserIncomeDTO queryIncome(Long userId) {
        return userAPI.queryIncome(userId);
    }

    public int queryRank(Long userId, String rankType) {
        return userAPI.queryRank(userId, rankType);
    }

    public List<UserInviteTreeDTO> queryUserTreeNode(Long userId, int level) {
        return userAPI.queryUserTreeNode(userId, level);
    }

    public void resetLoginPassword(Long userId, String password) {
        userAPI.resetLoginPassword(userId, password);
    }

    public UserStaDTO sta(Long userId, int level) {
        long timeMillis = System.currentTimeMillis();
        UserStaDTO sta = userAPI.sta(userId, level);
        OrderUserStaDTO orderSta = orderAPI.userSta(userId);
        sta.setSumAmount(orderSta.getSumAmount());
        sta.setSumProfit(orderSta.getSumProfit());
        sta.setPerProfit(orderSta.getPerProfit());
        sta.setHasGiveOrder(orderSta.getHasGiveOrder());
        log.info("查询用户统计耗时:{}ms", System.currentTimeMillis() - timeMillis);
        return sta;
    }

    public UserCountDTO newsta(Long userId) {
        long timeMillis = System.currentTimeMillis();
        UserCountDTO sta = userAPI.newsta(userId);
        log.info("查询用户统计耗时:{}ms", System.currentTimeMillis() - timeMillis);
        return sta;
    }

}
