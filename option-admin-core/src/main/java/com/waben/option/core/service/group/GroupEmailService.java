package com.waben.option.core.service.group;

import com.waben.option.common.interfaces.thirdparty.SmsAPI;
import com.waben.option.common.model.enums.EmailTypeEnum;
import com.waben.option.data.repository.user.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GroupEmailService {

    @Resource
    private UserDao userDao;

    @Resource
    private SmsAPI smsAPI;

    public void send(String strDate) {
        List<String> userList = userDao.queryList(strDate);
        int count = userList.size();
        for (int i = 0; i < userList.size(); i++) {
            String baseStr = userList.get(i);
            if (!StringUtils.isEmpty(baseStr)) {
                String[] strList = baseStr.split(",");
                Long userId = Long.valueOf(strList[0]);
                String email = strList[1];
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    smsAPI.sendCode(null, email, null, EmailTypeEnum.SPECIFIC_CONTENT, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("group_send_email:{}", e.getMessage());
                }
                userDao.deleteById(userId);
                log.info("总共：{}条，预发送{}条。 用户编号：{}--{}", count, i, userId, email);
            }
        }
    }

    private void test() {
        List<String> stringList = new ArrayList<>();
        stringList.add("winterwillpass@pm.me");
        stringList.add("winterwillpass.lau@gmail.com");
        stringList.add("angelina.solarpower@gmail.com");
        stringList.add("bigalanlin@gmail.com");
        stringList.add("xinyi5504@gmail.com");
        stringList.add("ada.solarpower@gmail.com");
        stringList.add("bigwinnerkk@gmail.com");
        for (String s : stringList) {
            smsAPI.sendCode(null, s, null, EmailTypeEnum.SPECIFIC_CONTENT, null, null);
        }
    }
}
