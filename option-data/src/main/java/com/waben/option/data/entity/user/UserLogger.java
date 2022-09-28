package com.waben.option.data.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.Logger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_u_user_logger", autoResultMap = true)
public class UserLogger extends Logger {

    private Long userId;

    public static final String USER_ID = "user_id";

    public static final String CMD = "cmd";

    public static final String IP = "ip";
}
