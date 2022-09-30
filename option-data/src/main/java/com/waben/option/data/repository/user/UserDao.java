package com.waben.option.data.repository.user;

import com.waben.option.common.model.dto.summary.UserFissionDataDTO;
import com.waben.option.common.model.dto.user.UserSymbolDTO;
import com.waben.option.common.model.dto.user.UserTreeDTO;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserDao extends BaseRepository<User> {

    @Select("SELECT CONCAT_WS(',',t3.id,t3.username)AS concat_name  FROM ( " +
            "SELECT " +
            "t1.id, t1.username, t1.`name`, t1.last_login_time " +
            "FROM t_u_user t1 " +
            "WHERE t1.last_login_time >= #{strDate} AND username LIKE '%@%' " +
            "UNION " +
            "SELECT t1.id, t1.email AS username, t1.`name`, t1.last_login_time FROM t_u_user t1 WHERE " +
            "t1.last_login_time >= #{strDate} AND username NOT LIKE '%@%' AND email IS NOT NULL AND email LIKE '%@%' " +
            ") AS t3 ORDER BY t3.last_login_time DESC")
    List<String> queryList(@Param("strDate") String strDate);

    /**
     * 根据父及代理查询这个层级下最大的编码
     *
     * @param parentId
     * @return
     */
    @Select("SELECT symbol FROM t_u_user WHERE parent_id=#{parentId} ORDER BY CONVERT(RIGHT(symbol,5),UNSIGNED) DESC limit 1")
    String queryMaxSymbol(@Param("parentId") Long parentId);

    @Select("SELECT symbol FROM t_u_user WHERE id=#{id} ORDER BY CONVERT(RIGHT(symbol,5),UNSIGNED) DESC limit 1")
    String queryMaxSymbolById(@Param("id") Long id);

    @Select("select id userId, symbol from t_u_user where symbol like concat(#{symbol}, '%')")
    List<UserSymbolDTO> queryUidList(@Param("symbol") String symbol);

    @Select("select id from t_u_user where gmt_create like concat(#{time}, '%')")
    List<Long> registerUserList(@Param("time") String time);

    @Select("select count(*) from t_u_user where gmt_create like concat(#{time}, '%')")
    Integer registerNumber(@Param("time") String time);

    @Select("select count(*) from t_u_user where parent_id > 0 and gmt_create like concat(#{time}, '%')")
    Integer inviteRegister(@Param("time") String time);

    @Select("select count(*) from t_u_user where parent_id > 0 and is_real=1 and gmt_create like concat(#{time}, '%')")
    Integer inviteRealRegister(@Param("time") String time);

    @Select("select id from t_u_user where gmt_create like concat(#{time}, '%') and parent_id != 0")
    List<Long> beInvitesCount(@Param("time") String time);

    @Select({"<script>",
            "<bind name='offset' value='(page-1) * size'></bind>",
            "SELECT tp.id AS userId,tp.mobile_phone mobilePhone, (SELECT COUNT(*) FROM t_u_user tc WHERE tc.parent_id = tp.id) AS directLevelCount FROM t_u_user tp " +
                    "where tp.id IN (SELECT ti.parent_id FROM t_u_user ti) ",
            "<if test='mobilePhone != null'>",
            "and tp.mobile_phone=#{mobilePhone}",
            "</if>",
            "limit #{offset}, #{size}",
            "</script>"})
    List<UserFissionDataDTO> userFissonList(@Param("mobilePhone") String mobilePhone, @Param("page") int page, @Param("size") int size);

    @Select({"<script>",
            "SELECT count(tp.id) count FROM t_u_user tp WHERE tp.id IN (SELECT ti.parent_id FROM t_u_user ti) ",
            "<if test='mobilePhone != null'>",
            "and tp.mobile_phone=#{mobilePhone}",
            "</if>",
            "</script>"})
    int userFissonCount(@Param("mobilePhone") String mobilePhone);

    @Select("select id from t_u_user where parent_id = #{userId}")
    List<Long> userDirectCount(@Param("userId") Long userId);

    @Select({"<script>" +
            "select id from t_u_user " +
            "<when test='uidList!=null'>" +
            " where parent_id in " +
            "<foreach collection='uidList' item='userId' open='(' separator=',' close=')'> " +
            "#{userId}" +
            "</foreach> " +
            "</when>" +
            "</script>"})
    List<Long> userInDirect(@Param("uidList") List<Long> uidList);

    @Select("SELECT * FROM `t_u_user` where id =#{userId} or (symbol LIKE CONCAT(#{symbol},'%') AND symbol != #{symbol} AND (LENGTH(symbol) - LENGTH(#{symbol})) <=5)")
    List<User> queryUserBySymbolLike(Long userId, String symbol);


    @Select("SELECT COUNT(id) AS invitePeople FROM `t_u_user` where " +
            " symbol = #{symbol} OR (symbol LIKE CONCAT(#{symbol},'0','%') AND (LENGTH(symbol) - LENGTH(#{symbol})) <=5)")
    Integer invitePeopleByUsers(@Param("symbol") String symbol);

    @Select("select username from t_u_user where id = #{userId}")
    String queryPhone(@Param("userId") Long userId);

    @Select({"<script>", "SELECT\n" +
            "<if test=\"level==1\">\n" +
            "a.id,a.parent_id AS parentId,a.group_index AS groupIndex,COALESCE(d.balance,0) AS balance," +
            "COALESCE" +
            "(d.commission,0) AS commission,COALESCE(d.second_commission,0) AS secondCommission,COALESCE(d.thrid_commission,0) " +
            "AS thridCommission,COALESCE(d.freeze_capital,0) AS freezeCapital,d.currency\n" +
            "</if>\n" +
            "<if test=\"level==2\">\n" +
            "b.id,a.id AS parentId,b.group_index AS groupIndex,COALESCE(d.balance,0) AS balance,COALESCE" +
            "(d.commission,0) AS commission,COALESCE(d.second_commission,0) AS secondCommission,COALESCE(d.thrid_commission,0) " +
            "AS thridCommission,COALESCE(d.freeze_capital,0) AS freezeCapital,d.currency\n" +
            "</if>\n" +
            "<if test=\"level==3\">\n" +
            "c.id,b.id AS parentId,c.group_index AS groupIndex,COALESCE(d.balance,0) AS balance,COALESCE(d" +
            ".commission,0) AS commission," +
            "COALESCE(d.second_commission,0) AS secondCommission,COALESCE(d.thrid_commission,0) AS thridCommission,COALESCE(d.freeze_capital,0) AS freezeCapital,d.currency\n" +
            "</if>\n" +
            "FROM `t_u_user` a\n" +
            "<if test=\"level==1\">\n" +
            "LEFT JOIN `t_u_account` d ON d.user_id=a.id\n" +
            "</if>\n" +
            "<if test=\"level==2\">\n" +
            "LEFT JOIN `t_u_user` b ON b.parent_id=a.id\n" +
            "LEFT JOIN `t_u_account` d ON d.user_id=b.id\n" +
            "</if>\n" +
            "<if test=\"level==3\">\n" +
            "LEFT JOIN `t_u_user` b ON b.parent_id=a.id\n" +
            "LEFT JOIN `t_u_user` c ON c.parent_id=b.id\n" +
            "LEFT JOIN `t_u_account` d ON d.user_id=c.id\n" +
            "</if>\n" +
            "WHERE a.parent_id=#{userId}<if test=\"level==3\"> AND c.id IS NOT NULL</if><if test=\"level==2\"> AND b" +
            ".id IS NOT NULL</if><if test=\"level==1\"> AND a.id IS NOT NULL</if>", "</script>"})
    List<UserTreeDTO> queryUserId(@Param("userId") Long userId, @Param("level") int level);

    @Select({"SELECT id,groupIndex,`level`,inviteAuditStatus\n" +
            "FROM\n" +
            "(SELECT\n" +
            "b.id,b.group_index AS groupIndex,1 AS `level`,a.invite_audit_status AS inviteAuditStatus\n" +
            "FROM `t_u_activity_user_join` a\n" +
            "LEFT JOIN `t_u_user` b ON b.id=a.join_user_id\n" +
            "LEFT JOIN `t_u_account` e ON e.user_id=a.user_id\n" +
            "WHERE b.parent_id=#{userId} AND a.activity_type='INVITE'\n" +
            "UNION ALL\n" +
            "SELECT\n" +
            "b.id,b.group_index AS groupIndex,2 AS `level`,a.invite_audit_status AS inviteAuditStatus\n" +
            "FROM `t_u_activity_user_join` a\n" +
            "LEFT JOIN `t_u_user` b ON b.id=a.join_user_id\n" +
            "LEFT JOIN `t_u_user` c ON c.id=b.parent_id\n" +
            "LEFT JOIN `t_u_account` e ON e.user_id=a.user_id\n" +
            "WHERE c.parent_id=#{userId} AND a.activity_type='INVITE'\n" +
            "UNION ALL\n" +
            "SELECT\n" +
            "b.id,b.group_index AS groupIndex,3 AS `level`,a.invite_audit_status AS inviteAuditStatus\n" +
            "FROM `t_u_activity_user_join` a\n" +
            "LEFT JOIN `t_u_user` b ON b.id=a.join_user_id\n" +
            "LEFT JOIN `t_u_user` c ON c.id=b.parent_id\n" +
            "LEFT JOIN `t_u_user` d ON d.id=c.parent_id\n" +
            "LEFT JOIN `t_u_account` e ON e.user_id=a.user_id\n" +
            "WHERE d.parent_id=#{userId}  AND a.activity_type='INVITE') f"})
    List<Map<String, Object>> queryUserCount(@Param("userId") Long userId);

}
