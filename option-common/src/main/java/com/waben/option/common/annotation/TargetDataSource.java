package com.waben.option.common.annotation;

import com.waben.option.common.model.enums.DataSourceType;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {

    DataSourceType value() default DataSourceType.DEFAULT;

    boolean isDatabaseShardingOnly() default true;
}
