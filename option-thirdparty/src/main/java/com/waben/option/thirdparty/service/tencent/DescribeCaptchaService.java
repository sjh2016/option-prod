package com.waben.option.thirdparty.service.tencent;

import com.tencentcloudapi.captcha.v20190722.CaptchaClient;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultRequest;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DescribeCaptchaService {

    @Value("${describe.captcha.url:captcha.tencentcloudapi.com}")
    private String url;

    @Value("${describe.captcha.secretId:AKIDOr9GLCT7BVgcK1j8OExTjxv40afc6bbq}")
    private String secretId;

    @Value("${describe.captcha.secretKey:A4iP4VkjI4bDk12oLde6OTIpHI6f2dz4}")
    private String secretKey;

    @Value("${describe.captcha.captchaAppId:2077734432}")
    private String captchaAppId;

    @Value("${describe.captcha.appSecretKey:07v_BfanQScmxGbMkIWjp4g**}")
    private String appSecretKey;

    public String verifyCredential(String ticket, String randStr, String userIp) {
        try {
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(url);
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            Credential cred = new Credential(secretId, secretKey);
            CaptchaClient client = new CaptchaClient(cred, "ap-guangzhou", clientProfile);
            DescribeCaptchaResultRequest req = new DescribeCaptchaResultRequest();
            req.setCaptchaAppId(Long.valueOf(captchaAppId));
            req.setAppSecretKey(appSecretKey);
            req.setUserIp(userIp);
            req.setCaptchaType(9L);
            req.setTicket(ticket);
            req.setRandstr(randStr);
            DescribeCaptchaResultResponse resp = client.DescribeCaptchaResult(req);
            return DescribeCaptchaResultResponse.toJsonString(resp);
        } catch (TencentCloudSDKException e) {
            log.error(e.toString());
        }
        return null;
    }
}
