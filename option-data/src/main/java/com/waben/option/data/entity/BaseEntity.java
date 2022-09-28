package com.waben.option.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class BaseEntity<ID> extends Entity {

    @TableId(type = IdType.ASSIGN_ID)
    private ID id;

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

    public static final String ID = "id";

    public static final String GMT_CREATE = "gmt_create";

    public static final String GMT_UPDATE = "gmt_update";

    public static final String VERSION = "version";

}
