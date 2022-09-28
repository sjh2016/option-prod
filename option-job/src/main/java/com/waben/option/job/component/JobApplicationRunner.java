package com.waben.option.job.component;

import com.waben.option.job.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
//@Component
public class JobApplicationRunner implements ApplicationRunner {

    private int tryCount;

    @Resource
    private JobService jobService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        do {
            try {
                tryCount++;
                /*if (!CollectionUtils.isEmpty(noticeList)) {
                    for (NoticeDTO notice : noticeList) {
                        Pair<String, String> overnightPair = jobService.getNoticeJobName(TimeLogoEnum.END_TIME, notice.getId());
                        jobService.updateNoticeJob(overnightPair.getLeft(), overnightPair.getRight(), notice.getEnd(), notice.getId(), TimeLogoEnum.END_TIME);
                    }
                    log.info("notice job started success");
                }*/
                return;
            } catch (Exception e) {
                log.error("", e);
            }
        } while (tryCount < 5);
    }

}
