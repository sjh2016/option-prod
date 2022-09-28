# 用户邀请人功能，新增关联字段
ALTER TABLE `option_solar`.`t_u_user`
ADD COLUMN `parent_symbol_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '父级代码' AFTER `real_parent_id`;

ALTER TABLE `option_solar`.`t_u_user`
ADD INDEX `t_u_user_parent_symbol_code_index`(`parent_symbol_code`) USING BTREE;


