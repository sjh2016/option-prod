package com.waben.option.service.code;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.SmsAPI;
import com.waben.option.common.interfacesadmin.resource.AdminConfigAPI;
import com.waben.option.common.interfacesadmin.resource.AdminImageCodeAPI;
import com.waben.option.common.model.dto.resource.ImageCodeDTO;
import com.waben.option.common.model.enums.EmailTypeEnum;
import com.waben.option.common.model.vo.slider.SliderImageVO;
import com.waben.option.common.util.NumberUtil;
import com.waben.option.common.util.SliderVerifyUtil;

@Service
public class CodeService {

    @Resource
    private SmsAPI smsAPI;

    @Resource
    private AdminImageCodeAPI adminImageCodeAPI;

    @Resource
    private AdminConfigAPI adminConfigAPI;

    public ImageCodeDTO generateImageCode() {
        return adminImageCodeAPI.generate(4);
    }

    public void send(String areaCode, String username, EmailTypeEnum type, String content, String ip) {
        /*if (userAPI.verifyUsername(username)) {
            throw new ServerException(1016);
        }*/
        smsAPI.sendCode(areaCode, username, NumberUtil.generateCode(6), type, content, ip);
    }

    public void verify(String mobilePhone, String code) {
        smsAPI.verifyCode(mobilePhone, code);
    }

    public String verifyCredential(String ticket, String randStr, String userIp) {
        return smsAPI.verifyCredential(ticket, randStr, userIp);
    }

    public boolean verifyImageCode(String sessionId, String code) {
        return adminImageCodeAPI.verify(sessionId, code);
    }

    public boolean verifyNotDeleteImageCode(String sessionId, String code) {
        return adminImageCodeAPI.verifyNotDeleteCode(sessionId, code);
    }

    public String queryCode(Long currentUserId, String mobilePhone) {
        return smsAPI.queryCode(currentUserId, mobilePhone);
    }

    public SliderImageVO createSliderImage(String url) {
        File file = null;
        File imageUrl;
        if (StringUtils.isEmpty(url)) {
            String uploadFilePath = adminConfigAPI.queryUploadPathConfig();
            if (uploadFilePath != null) uploadFilePath = uploadFilePath + "slider";
            try {
                //??????????????????????????????,???????????????
                file = ResourceUtils.getFile(uploadFilePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            File[] files = file.listFiles();
            int n = new Random().nextInt(files.length);
            imageUrl = files[n];
        } else {
            imageUrl = SliderVerifyUtil.getFile(url);
        }
        //??????????????????
        //SliderVerifyUtil.createImage("http://hbimg.b0.upaiyun.com/7986d66f29bfeb6015aaaec33d33fcd1d875ca16316f-2bMHNG_fw658",resultMap);
        return SliderVerifyUtil.createImage(imageUrl);
    }

    public void verifyImageCode(String moveLength, Integer xWidth) {
        Double dMoveLength = Double.valueOf(moveLength);
        if (xWidth == null) {
            throw new ServerException(1049);

        }
        if (Math.abs(xWidth - dMoveLength) > 10) {
            throw new ServerException(1050);
        }
    }
}
