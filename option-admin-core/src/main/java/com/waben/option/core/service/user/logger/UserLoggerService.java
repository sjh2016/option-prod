package com.waben.option.core.service.user.logger;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.user.logger.LoggerCommandDTO;
import com.waben.option.common.model.dto.user.logger.UserLoggerDTO;
import com.waben.option.common.model.request.user.UserLoggerRequest;
import com.waben.option.data.entity.resource.LoggerCommand;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.entity.user.UserLogger;
import com.waben.option.data.repository.resource.LoggerCommandDao;
import com.waben.option.data.repository.user.UserLoggerDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLoggerService {

    @Resource
    private UserLoggerDao userLoggerDao;

    @Resource
    private LoggerCommandDao loggerCommandDao;

    @Resource
    private ModelMapper modelMapper;

    public PageInfo<UserLoggerDTO> queryUserLoggerPage(UserLoggerRequest request) {
        QueryWrapper<UserLogger> query = new QueryWrapper<>();
        if (request.getUidList() != null && request.getUidList().size() > 0) {
            query = query.in(UserLogger.USER_ID, request.getUidList());
        }
        if (!StringUtils.isEmpty(request.getStartTime())) {
            query = query.ge(UserLogger.GMT_CREATE, request.getStartTime());
        }
        if (!StringUtils.isEmpty(request.getEndTime())) {
            query = query.le(UserLogger.GMT_CREATE, request.getEndTime());
        }
        if (!StringUtils.isEmpty(request.getIp())) {
            query = query.eq(UserLogger.IP, request.getIp());
        }
        if (!CollectionUtils.isEmpty(request.getCmdList())) {
            query = query.in(UserLogger.CMD, request.getCmdList());
        }
        query = query.orderByDesc(User.GMT_CREATE);
        PageInfo<UserLoggerDTO> pageInfo = new PageInfo<>();
        IPage<UserLogger> userLoggerIPage = userLoggerDao.selectPage(new Page<>(request.getPage(), request.getSize()), query);
        if (userLoggerIPage.getTotal() > 0) {
            List<UserLoggerDTO> userList = userLoggerIPage.getRecords().stream().map(user -> modelMapper.map(user, UserLoggerDTO.class))
                    .collect(Collectors.toList());
            pageInfo.setRecords(userList);
            pageInfo.setTotal(userLoggerIPage.getTotal());
            pageInfo.setPage((int) userLoggerIPage.getPages());
            pageInfo.setSize((int) userLoggerIPage.getSize());
            return pageInfo;
        }
        return pageInfo;
    }

    public List<LoggerCommandDTO> queryLoggerAction(List<String> cmdList, String platform) {
        QueryWrapper<LoggerCommand> queryWrapper = new QueryWrapper<>();
        if (!CollectionUtils.isEmpty(cmdList)) {
            queryWrapper = queryWrapper.in(LoggerCommand.CMD, cmdList);
        }
        if (!StringUtils.isEmpty(platform)) {
            queryWrapper = queryWrapper.eq(LoggerCommand.PLATFORM, platform);
        }
        List<LoggerCommand> commandList = loggerCommandDao.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(commandList)) {
            List<LoggerCommandDTO> commandDTOList = commandList
                    .stream().map(command -> modelMapper.map(command, LoggerCommandDTO.class)).collect(Collectors.toList());
            return commandDTOList;
        }
        return null;
    }
}
