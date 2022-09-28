package com.waben.option.common.model.query;

import com.waben.option.common.model.enums.TransactionEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserAccountStatementQuery {

    private String username;

    private String name;

    private List<Long> uidList;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private TransactionEnum[] type;

    private int page;

    private int size;
    
    private int level;
}
