package com.waben.option.common.model.request.user;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserLoggerRequest {

    private List<Long> uidList;
    private List<String> cmdList;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long brokerId;
    private String ip;
    private int page;
    private int size;
}
