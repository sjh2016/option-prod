package com.waben.option.core.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

@Slf4j
public class MyRoutingDataSource extends AbstractRoutingDataSource {

    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        log.info("当前数据连接池为：" + DBContextHolder.get());
        return DBContextHolder.get();
    }
}
