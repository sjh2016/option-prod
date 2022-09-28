package com.waben.option.common.model.query;

import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.common.model.enums.RegisterEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserPageQuery {

    private String name;

    private String username;

    private List<Long> idList;

    private RegisterEnum registerType;

    private AuthorityEnum authorityType;

    private LocalDateTime registerStart;

    private LocalDateTime registerEnd;

    private LocalDateTime lastLoginStart;

    private LocalDateTime lastLoginEnd;

    private Integer source;

    private int page;

    private int size;

    private String topId;
}
