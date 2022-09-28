package com.waben.option.data.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Entity {

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    @TableField(update = "now()")
    private LocalDateTime gmtUpdate;

    @Version
    private int version;

    public static final String GMT_CREATE = "gmt_create";

    public static final String GMT_UPDATE = "gmt_update";

    public static final String VERSION = "version";

}
