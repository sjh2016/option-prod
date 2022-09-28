package com.waben.option.core.controller.create.table;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/create/table")
public class CreateTableController {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/account_statement")
    public String createTable() {
        jdbcTemplate.update(
                "CREATE TABLE `t_u_account_statement` (\n" +
                        "  `id` bigint(20) NOT NULL,\n" +
                        "  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',\n" +
                        "  `account_id` bigint(20) DEFAULT NULL COMMENT '账户ID',\n" +
                        "  `amount` decimal(20,2) DEFAULT NULL COMMENT '金额',\n" +
                        "  `balance` decimal(20,2) DEFAULT NULL COMMENT '账户余额',\n" +
                        "  `freeze_capital` decimal(20,2) DEFAULT NULL COMMENT '冻结金额',\n" +
                        "  `type` varchar(255) DEFAULT NULL COMMENT '流水类型',\n" +
                        "  `credit_debit` varchar(255) DEFAULT NULL COMMENT '资金变化类型',\n" +
                        "  `transaction_id` bigint(20) DEFAULT NULL COMMENT '关联ID',\n" +
                        "  `unique_id` bigint(20) DEFAULT NULL COMMENT '唯一ID',\n" +
                        "  `currency` varchar(255) DEFAULT NULL COMMENT '币种',\n" +
                        "  `remark` varchar(2000) DEFAULT NULL COMMENT '备注',\n" +
                        "  `test` bit(1) DEFAULT NULL,\n" +
                        "  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                        "  `version` int(11) unsigned DEFAULT '0' COMMENT '版本号',\n" +
                        "  PRIMARY KEY (`id`) USING BTREE,\n" +
                        "  KEY `index2` (`user_id`,`type`,`gmt_create`) USING BTREE,\n" +
                        "  KEY `index3` (`type`,`gmt_create`) USING BTREE\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;");
        return "success";
    }
}
