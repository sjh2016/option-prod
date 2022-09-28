package com.waben.option.data.repository.statement;

import com.waben.option.data.entity.statement.AccountStatement6;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface AccountStatementDao6 extends AccountStatementStaDao<AccountStatement6> {

    @Override
    @Select({"<script>", "SELECT\n" +
            "COALESCE(SUM(e.amount),0) AS inviteAmount\n" +
            "FROM `t_u_user` b\n" +
            "LEFT JOIN (SELECT user_id,invite_audit_status,statement_id,join_user_id FROM `t_u_activity_user_join` WHERE activity_type='INVITE') a ON a.join_user_id =b.id\n" +
            "LEFT JOIN (SELECT id,user_id,amount FROM `t_u_account_statement6` WHERE `type` IN ('CREDIT_INVITE_WAGER', 'CREDIT_SUBORDINATE')) e ON e.user_id=b.id\n" +
            "WHERE b.id=#{userId} AND a.invite_audit_status='PASS'", "</script>"})
    BigDecimal querySta(@Param("userId") Long userId, @Param("level") int level);

}
