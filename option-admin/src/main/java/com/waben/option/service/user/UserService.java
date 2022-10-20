package com.waben.option.service.user;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.waben.option.common.interfaces.user.UserAPI;
import com.waben.option.common.model.dto.user.UserInviteTreeDTO;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfacesadmin.user.AdminUserAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.user.UserDTO;
import com.waben.option.common.model.dto.user.UserIncomeDTO;
import com.waben.option.common.model.dto.user.UserTreeNodeDTO;
import com.waben.option.common.model.query.UserPageQuery;
import com.waben.option.common.model.request.user.RegisterUserRequest;
import com.waben.option.common.model.request.user.UpdatePassword1Request;
import com.waben.option.common.model.request.user.UpdatePassword2Request;
import com.waben.option.common.model.request.user.UpdateUserBasicRequest;
import com.waben.option.common.util.CloneUtils;
import com.waben.option.mode.request.ClientRegisterUserRequest;
import com.waben.option.mode.request.ClientUpdatePassword1Request;
import com.waben.option.mode.request.ClientUpdatePassword2Request;
import com.waben.option.mode.request.ClientUpdateUserBasicRequest;
import com.waben.option.mode.vo.UserVO;

@Service
public class UserService {

    @Resource
    private AdminUserAPI adminUserAPI;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private UserAPI userAPI;

    public UserVO register(ClientRegisterUserRequest clientRequest) {
        RegisterUserRequest request = modelMapper.map(clientRequest, RegisterUserRequest.class);
        UserDTO userDTO = adminUserAPI.register(request);
        return modelMapper.map(userDTO, UserVO.class);
    }

    public void updateLoginPassword(ClientUpdatePassword1Request clientRequest) {
        UpdatePassword1Request request = modelMapper.map(clientRequest, UpdatePassword1Request.class);
        adminUserAPI.updateLoginPassword(request);
    }

    public void updateLoginPassword(ClientUpdatePassword2Request clientRequest) {
        UpdatePassword2Request request = modelMapper.map(clientRequest, UpdatePassword2Request.class);
        if (StringUtils.isBlank(request.getVerifyCode())) {
            throw new ServerException(1026);
        }
        if (StringUtils.isBlank(request.getNewPassword())) {
            throw new ServerException(1025);
        }
        adminUserAPI.updateLoginPassword(request);
    }

    public void updateFundPassword(ClientUpdatePassword1Request clientRequest) {
        UpdatePassword1Request request = modelMapper.map(clientRequest, UpdatePassword1Request.class);
        if (StringUtils.isBlank(request.getNewPassword())) {
            throw new ServerException(1025);
        }
        adminUserAPI.updateFundPassword(request);
    }

    public void updateFundPassword(ClientUpdatePassword2Request clientRequest) {
        UpdatePassword2Request request = modelMapper.map(clientRequest, UpdatePassword2Request.class);
        if (StringUtils.isBlank(request.getVerifyCode())) {
            throw new ServerException(1026);
        }
        if (StringUtils.isBlank(request.getNewPassword())) {
            throw new ServerException(1025);
        }
        adminUserAPI.updateFundPassword(request);
    }

    public UserVO queryUser(Long userId) {
        UserDTO user = adminUserAPI.queryUser(userId);
        return modelMapper.map(user, UserVO.class);
    }

    public PageInfo<UserVO> queryUserPage(UserPageQuery userQuery) {
        PageInfo<UserDTO> pageInfo = adminUserAPI.queryUserPage(userQuery);
        return CloneUtils.copy(pageInfo, UserVO.class);
    }

    public Map<String,Integer> queryUserCount(UserPageQuery userQuery) {
        return adminUserAPI.queryUserCount(userQuery);
    }

    public Boolean verifyUsername(String username) {
        return adminUserAPI.verifyUsername(username);
    }

    public void updateUserBasic(ClientUpdateUserBasicRequest clientRequest) {
        UpdateUserBasicRequest request = modelMapper.map(clientRequest, UpdateUserBasicRequest.class);
        adminUserAPI.updateUserBasic(request);
    }

    public UserIncomeDTO queryIncome(Long userId) {
        return adminUserAPI.queryIncome(userId);
    }

    public int queryRank(Long userId, String rankType) {
        return adminUserAPI.queryRank(userId, rankType);
    }

    public List<UserTreeNodeDTO> queryUserTreeNode(Long userId) {
        return adminUserAPI.queryUserTreeNode(userId);
    }

    public List<UserInviteTreeDTO> queryUserTreeNode(Long userId, int level) {
        return userAPI.queryUserTreeNode(userId, level);
    }

    public PageInfo<UserInviteTreeDTO> queryUserTreeNodeNew(Long userId, int level, Long childUserId, int page ,int size) {
        return userAPI.queryUserTreeNodeNew(userId, level,childUserId,page,size);
    }

    public void resetLoginPassword(Long userId, String password) {
        adminUserAPI.resetLoginPassword(userId, password);
    }
}
