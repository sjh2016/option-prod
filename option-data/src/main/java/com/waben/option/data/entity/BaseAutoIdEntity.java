package com.waben.option.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class BaseAutoIdEntity extends Entity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    public static final String ID = "id";

}
