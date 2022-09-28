package com.waben.option.job.controller;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.job.service.JobService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: Peter
 * @date: 2021/5/14 14:39
 */
@Slf4j
//@RestController
//@RequestMapping("/job")
//@Api(tags = {"定时任务接口"})
public class JobController extends AbstractBaseController {

    @Resource
    private JobService jobService;

    /*@RequestMapping(value = "/upset/notice", method = RequestMethod.GET)
    public ResponseEntity<?> upsetNoticeJob(@RequestParam("timeLogo") TimeLogoEnum timeLogo, @RequestParam("time") LocalDateTime time, @RequestParam("noticeId") Long noticeId) {
        Pair<String, String> pair = jobService.getNoticeJobName(timeLogo, noticeId);
        jobService.removeJob(pair.getLeft(), pair.getRight());
        Pair<String, String> pair1 = jobService.getNoticeJobName(timeLogo, noticeId);
        jobService.updateNoticeJob(pair1.getLeft(), pair1.getRight(), time, noticeId, timeLogo);
        return ok();
    }

    @RequestMapping(value = "/delete/notice", method = RequestMethod.GET)
    public ResponseEntity<?> deleteNoticeJob(@RequestParam("timeLogo") TimeLogoEnum timeLogo, @RequestParam("noticeId") Long noticeId) {
        Pair<String, String> pair = jobService.getNoticeJobName(timeLogo, noticeId);
        jobService.removeJob(pair.getLeft(), pair.getRight());
        return ok();
    }*/
}
