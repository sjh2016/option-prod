package com.waben.option.data.repository.activity;

import com.waben.option.common.model.dto.user.UserInviteTreeDTO;
import com.waben.option.data.entity.activity.ActivityUserJoin;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ActivityUserJoinDao extends BaseRepository<ActivityUserJoin> {

    @Select({"<script>", "SELECT \n" +
            "b.id,b.username,b.mobile_phone AS mobilePhone,b.email,b.nickname,b.symbol,b.symbol_code AS symbolCode,b.parent_id AS parentId,a.invite_audit_status AS inviteAuditStatus,b.group_index AS groupIndex\n" +
            "FROM `t_u_activity_user_join` a\n" +
            "LEFT JOIN `t_u_user` b ON b.id=a.join_user_id\n" +
            "<if test=\"level==1\">\n" +
            "WHERE b.parent_id=#{userId}\n" +
            "</if>\n" +
            "<if test=\"level==2\">\n" +
            "LEFT JOIN `t_u_user` c ON c.id=b.parent_id\n" +
            "WHERE c.parent_id=#{userId}\n" +
            "</if>\n" +
            "<if test=\"level==3\">\n" +
            "LEFT JOIN `t_u_user` c ON c.id=b.parent_id\n" +
            "LEFT JOIN `t_u_user` d ON d.id=c.parent_id\n" +
            "WHERE d.parent_id=#{userId}\n" +
            "</if><if test=\"inviteAuditStatus!=null and " +
            "inviteAuditStatus!=''\">and a.invite_audit_status=#{inviteAuditStatus}</if>", "</script>"})
    List<UserInviteTreeDTO> queryList(@Param("userId") long userId, @Param("level") int level, @Param(
            "inviteAuditStatus") String inviteAuditStatus);



    @Select("SELECT count(1)" +
            " FROM `t_u_user` b" +
            " WHERE b.parent_id = #{userId}")
    Integer queryListDemoLevelCount(@Param("userId") Long userId,@Param("childUserId") Long childUserId);


    @Select("SELECT b.id,b.username,b.mobile_phone AS mobilePhone,b.email,b.nickname,b.symbol,b.symbol_code AS symbolCode,b.parent_id AS parentId,b.group_index AS groupIndex" +
            " FROM `t_u_user` b" +
            " WHERE b.parent_id = #{userId} limit #{page},#{size}")
    List<UserInviteTreeDTO> queryListDemoLevel(@Param("userId") Long userId,@Param("page") Integer page,@Param("size") Integer size,@Param("childUserId") Long childUserId);



    @Select("SELECT count(1)" +
            " FROM `t_u_user` b" +
            " LEFT JOIN `t_u_user` c ON c.id=b.parent_id" +
            " WHERE c.parent_id=#{userId}")
    Integer queryListDemoLeve2Count(@Param("userId") Long userId,@Param("childUserId") Long childUserID);

    @Select("SELECT b.id,b.username,b.mobile_phone AS mobilePhone,b.email,b.nickname,b.symbol,b.symbol_code AS symbolCode,b.parent_id AS parentId,b.group_index AS groupIndex" +
            " FROM `t_u_user` b" +
            " LEFT JOIN `t_u_user` c ON c.id=b.parent_id" +
            " WHERE c.parent_id=#{userId} limit #{page},#{size}")
    List<UserInviteTreeDTO> queryListDemoLeve2(@Param("userId") Long userId,@Param("page") Integer page,@Param("size") Integer size,@Param("childUserId") Long childUserId);


    @Select("SELECT count(1)" +
            " FROM `t_u_user` b" +
            " LEFT JOIN `t_u_user` c ON c.id=b.parent_id" +
            " LEFT JOIN `t_u_user` d ON d.id=c.parent_id" +
            " WHERE d.parent_id=#{userId} ")
    Integer  queryListDemoLeve3Count(@Param("userId") Long userId,@Param("childUserId") Long childUserId);

    @Select("SELECT b.id,b.username,b.mobile_phone AS mobilePhone,b.email,b.nickname,b.symbol,b.symbol_code AS symbolCode,b.parent_id AS parentId,b.group_index AS groupIndex" +
            " FROM `t_u_user` b" +
            " LEFT JOIN `t_u_user` c ON c.id=b.parent_id" +
            " LEFT JOIN `t_u_user` d ON d.id=c.parent_id" +
            " WHERE d.parent_id=#{userId} limit #{page},#{size}")
    List<UserInviteTreeDTO> queryListDemoLeve3(@Param("userId") Long userId,@Param("page") Integer page,@Param("size") Integer size,@Param("childUserId") Long childUserIdss);


}
