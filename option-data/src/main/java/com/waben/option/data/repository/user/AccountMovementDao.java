package com.waben.option.data.repository.user;

import com.waben.option.common.model.dto.account.AccountMovementDTO;
import com.waben.option.common.model.request.user.UserAccountMovementRequest;
import com.waben.option.data.entity.user.AccountMovement;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: Peter
 * @date: 2021/7/10 16:41
 */
@Mapper
public interface AccountMovementDao extends BaseRepository<AccountMovement> {

    @Select({"<script>",
            "<bind name='offset' value='(req.page-1) * req.size'></bind>",
            "select t1.*, t2.username, t2.name from t_u_account_movement t1 left join t_u_user t2 on t1.user_id=t2.id where 1=1 ",
            "<if test='req.statusList != null and req.statusList.size > 0'>",
            "and t1.status in",
            "<foreach collection='req.statusList' item='status' index='index' open='(' close=')' separator=','>",
            "#{status}",
            "</foreach>",
            "</if>",
            "<if test='req.uidList != null and req.uidList.size > 0'>",
            "and t1.user_id in",
            "<foreach collection='req.uidList' item='uid' index='index' open='(' close=')' separator=','>",
            "#{uid}",
            "</foreach>",
            "</if>",
            "<if test='req.creditDebit != null'>",
            "and t1.credit_debit=#{req.creditDebit}",
            "</if>",
            "<if test='req.startTime != null'>",
            "and t1.gmt_create &gt;= #{req.startTime}",
            "</if>",
            "<if test='req.endTime != null'>",
            "and t1.gmt_create &lt; #{req.endTime}",
            "</if>",
            "<if test='req.auditStart != null'>",
            "and t1.gmt_audit &gt;= #{req.auditStart}",
            "</if>",
            "<if test='req.auditEnd != null'>",
            "and t1.gmt_audit &lt; #{req.auditEnd}",
            "</if>",
            "<if test='req.id != null'>",
            "and t1.id=#{req.id}",
            "</if>",
            "<if test='req.username != null'>",
            "and t2.username=#{req.username}",
            "</if>",
            "order by t1.gmt_create desc limit #{offset}, #{req.size}",
            "</script>"})
    List<AccountMovementDTO> page(UserAccountMovementRequest req);

    @Select({"<script>",
            "select count(*) from t_u_account_movement t1 left join t_u_user t2 on t1.user_id=t2.id where 1=1 ",
            "<if test='req.statusList != null and req.statusList.size > 0'>",
            "and t1.status in",
            "<foreach collection='req.statusList' item='status' index='index' open='(' close=')' separator=','>",
            "#{status}",
            "</foreach>",
            "</if>",
            "<if test='req.uidList != null and req.uidList.size > 0'>",
            "and t1.user_id in",
            "<foreach collection='req.uidList' item='uid' index='index' open='(' close=')' separator=','>",
            "#{uid}",
            "</foreach>",
            "</if>",
            "<if test='req.creditDebit != null'>",
            "and t1.credit_debit=#{req.creditDebit}",
            "</if>",
            "<if test='req.startTime != null'>",
            "and t1.gmt_create &gt;= #{req.startTime}",
            "</if>",
            "<if test='req.endTime != null'>",
            "and t1.gmt_create &lt; #{req.endTime}",
            "</if>",
            "<if test='req.auditStart != null'>",
            "and t1.gmt_audit &gt;= #{req.auditStart}",
            "</if>",
            "<if test='req.auditEnd != null'>",
            "and t1.gmt_audit &lt; #{req.auditEnd}",
            "</if>",
            "<if test='req.id != null'>",
            "and t1.id=#{req.id}",
            "</if>",
            "<if test='req.username != null'>",
            "and t2.username=#{req.username}",
            "</if>",
            "</script>"})
    Long count(UserAccountMovementRequest req);
}
