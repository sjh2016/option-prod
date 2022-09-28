package com.waben.option.core.service.summary;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.IncomeRankDTO;
import com.waben.option.common.model.dto.summary.UserDataDTO;
import com.waben.option.data.entity.resource.IncomeRank;
import com.waben.option.data.entity.summary.UserData;
import com.waben.option.data.repository.payment.PaymentOrderDao;
import com.waben.option.data.repository.summary.UserDataDao;
import com.waben.option.data.repository.user.UserDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDataService {

    @Resource
    private UserDataDao userDataDao;

    @Resource
    private UserDao userDao;
    
    @Resource
    private PaymentOrderDao paymentOrderDao;

    @Resource
    private IdWorker idWorker;

    @Resource
    private ModelMapper modelMapper;

    public PageInfo<UserDataDTO> queryList(String startTime,String endTime, int page, int size) {
        QueryWrapper<UserData> query = new QueryWrapper<>();
        if (startTime != null) {
            query = query.ge(UserData.DAY,startTime);
        }
        if (startTime != null) {
            query = query.le(UserData.DAY,endTime);
        }
        query.orderByDesc(UserData.GMT_CREATE);
        PageInfo<UserDataDTO> pageInfo = new PageInfo<>();
        IPage<UserData> userDataIPage = userDataDao.selectPage(new Page<>(page,size), query);
        UserData userData1 = queryUserData();
        List<UserDataDTO> userDataDTOList = new ArrayList<>();
        List<UserData> userDataList = new ArrayList<>();
        if (userDataIPage.getTotal() > 0) {
            userDataList = userDataIPage.getRecords();
            if (userData1 != null) {
                userDataList.add(0, userData1);
            }
        } else {
            if (userData1 != null) {
                userDataList.add(userData1);
            }
        }
        if (userDataList.size() > 0) {
            for (UserData userData2 : userDataList ) {
                UserDataDTO userDataDTO = modelMapper.map(userData2,UserDataDTO.class);
                userDataDTOList.add(userDataDTO);
            }
            pageInfo.setRecords(userDataDTOList);
            pageInfo.setTotal(userDataIPage.getTotal() + 1);
            pageInfo.setPage((int) userDataIPage.getPages());
            pageInfo.setSize((int) userDataIPage.getSize());
        }
        return pageInfo;
    }

    public UserData queryUserData() {
        UserData userData = new UserData();
        String time = LocalDate.now().plusDays(0).toString();
        userData.setId(idWorker.nextId());
        userData.setDay(time);
        // 注册人数
        userData.setRegisterNumber(registerCount(time));
        userData.setPaymentAmount(paymentAmoutn(time));
        userData.setPaymentUserCount(paymentUserCount(time));
        userData.setBeInvites(beInvitesCount(time));
        userData.setBeInvitesPaymentAmount(beInvitesPayment(time));
        return userData;
    }

    public void create() {
        String time = LocalDate.now().plusDays(-1).toString();
        UserData userData = new UserData();
        userData.setId(idWorker.nextId());
        userData.setDay(time);
        // 注册人数
        userData.setRegisterNumber(registerCount(time));
        userData.setPaymentAmount(paymentAmoutn(time));
        userData.setPaymentUserCount(paymentUserCount(time));
        userData.setBeInvites(beInvitesCount(time));
        userData.setBeInvitesPaymentAmount(beInvitesPayment(time));
        userDataDao.insert(userData);
    }

    /**
     * 获取每天注册人数
     * @param time
     * @return
     */
    public Integer registerCount(String time) {
        List<Long> uidList = userDao.registerUserList(time);
        return uidList.size();
    }

    /**
     * 获取每天入金金额
     * @param time
     * @return
     */
    public BigDecimal paymentAmoutn(String time) {
        List<Long> uidList = userDao.registerUserList(time);
        BigDecimal payAmount = BigDecimal.ZERO;
        if (uidList != null && uidList.size() > 0) {
            payAmount = paymentOrderDao.queryPayAmount(time,uidList);
        }
        return payAmount;
    }

    /**
     * 被邀请人数
     * @param time
     * @return
     */
    public Integer beInvitesCount(String time) {
        List<Long> uidList = userDao.beInvitesCount(time);
        return uidList.size();
    }

    /**
     * 被邀请人入金金额
     * @param time
     * @return
     */
    public BigDecimal beInvitesPayment(String time) {
        List<Long> uidList = userDao.beInvitesCount(time);
        BigDecimal paymentAmount = BigDecimal.ZERO;
        if (uidList != null && uidList.size() > 0) {
            paymentAmount = paymentOrderDao.queryPayAmount(time,uidList);
        }
        return paymentAmount;
    }

    /**
     * 入金人数
     * @param time
     * @return
     */
    public Integer paymentUserCount(String time) {
        return paymentOrderDao.queryPayCountTotal(time);
    }

}
