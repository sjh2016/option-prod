package com.waben.option.thirdparty.service.outpush;

import com.waben.option.common.constants.DBConstants;
import com.waben.option.common.interfaces.resource.ConfigAPI;
import com.waben.option.common.interfaces.user.UserVestAPI;
import com.waben.option.common.model.dto.push.OutsideNoticeDTO;
import com.waben.option.common.model.dto.resource.ConfigDTO;
import com.waben.option.common.model.dto.user.UserVestDTO;
import com.waben.option.common.model.dto.vest.AppVestDTO;
import com.waben.option.common.model.dto.vest.AppVestResponse;
import com.waben.option.common.util.HttpUtil;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.OutsidePushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OneSignalOutsidePushService implements OutsidePushService {

    @Resource
    private ConfigAPI configAPI;

    @Resource
    private UserVestAPI userVestAPI;

    @Override
    public void notifications(OutsideNoticeDTO notice) {
        log.info("start push notifications {}", notice);
        String appVestUrl = null;
        ConfigDTO config = configAPI.queryConfig(DBConstants.APP_VEST_URL);
        if (config != null && config.getValue() != null) {
            appVestUrl = config.getValue().trim();
        }
        if (appVestUrl != null && !"".equals(appVestUrl)) {
            if (notice.getUserIds() != null && notice.getUserIds().size() > 0) {
                // 点对点
                List<UserVestDTO> vestList = userVestAPI.query(notice.getUserIds());
                if (vestList != null && vestList.size() > 0) {
                    for (UserVestDTO vest : vestList) {
                        AppVestDTO appVest = getAppVest(appVestUrl, vest.getDeviceType(), vest.getVestIndex());
                        if (appVest != null && !StringUtils.isEmpty(appVest.getJPushAppKey())
                                && !StringUtils.isEmpty(appVest.getJPushSecret())) {
                            point(appVest, notice, vest.getUserId());
                        }
                    }
                }
            } else {
                // 广播
                List<UserVestDTO> vestList = configAPI.queryOutsideBroadcastVestList();
                if (vestList != null && vestList.size() > 0) {
                    for (UserVestDTO vest : vestList) {
                        AppVestDTO appVest = getAppVest(appVestUrl, vest.getDeviceType(), vest.getVestIndex());
                        if (appVest != null && !StringUtils.isEmpty(appVest.getJPushAppKey())
                                && !StringUtils.isEmpty(appVest.getJPushSecret())) {
                            broadcast(appVest, notice);
                        }
                    }
                }
            }
        } else {
            log.info("appVestUrl config not exit!");
        }
        log.info("end push notifications {}", notice);
    }

    private AppVestDTO getAppVest(String appVestUrl, Integer deviceType, Integer vestIndex) {
        String url = appVestUrl + "?deviceType=" + deviceType + "&vestIndex=" + vestIndex;
        return HttpUtil.requestGet(url, new HttpUtil.ResponseDataExecutor<AppVestDTO>() {
            @Override
            public AppVestDTO execute(String data) {
                log.info("{} getAppVest data: {}", url, data);
                AppVestResponse<AppVestDTO> resp = JacksonUtil.decode(data, AppVestResponse.class, AppVestDTO.class);
                if (resp.getResult() != null) {
                    return resp.getResult();
                } else {
                    return null;
                }
            }
        });
    }

    private void point(AppVestDTO appVest, OutsideNoticeDTO notice, Long userId) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json; charset=utf-8");
        headerMap.put("Authorization", "Basic " + appVest.getJPushSecret());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("app_id", appVest.getJPushAppKey());
        Map<String, String> headings = new HashMap<>();
        headings.put("en", notice.getTitle());
        paramMap.put("headings", headings);
        Map<String, String> contents = new HashMap<>();
        contents.put("en", notice.getContent());
        paramMap.put("contents", contents);
        paramMap.put("channel_for_external_user_ids", "push");
        paramMap.put("include_external_user_ids", Arrays.asList(String.valueOf(userId)));
        HttpUtil.requestPostJsonData("https://onesignal.com/api/v1/notifications", headerMap, paramMap,
                new HttpUtil.ResponseDataExecutor<String>() {
                    @Override
                    public String execute(String data) {
                        return null;
                    }
                });
    }

    private void broadcast(AppVestDTO appVest, OutsideNoticeDTO notice) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json; charset=utf-8");
        headerMap.put("Authorization", "Basic " + appVest.getJPushSecret());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("app_id", appVest.getJPushAppKey());
        Map<String, String> headings = new HashMap<>();
        headings.put("en", notice.getTitle());
        paramMap.put("headings", headings);
        Map<String, String> contents = new HashMap<>();
        contents.put("en", notice.getContent());
        paramMap.put("contents", contents);
        paramMap.put("included_segments", Arrays.asList("Subscribed Users"));
        HttpUtil.requestPostJsonData("https://onesignal.com/api/v1/notifications", headerMap, paramMap,
                new HttpUtil.ResponseDataExecutor<String>() {
                    @Override
                    public String execute(String data) {
                        return null;
                    }
                });
    }

}
