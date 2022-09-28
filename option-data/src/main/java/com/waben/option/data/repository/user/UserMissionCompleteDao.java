package com.waben.option.data.repository.user;

import com.waben.option.common.model.dto.summary.FundDataDTO;
import com.waben.option.data.entity.user.UserMissionComplete;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author: Peter
 * @date: 2021/7/16 17:28
 */
@Mapper
public interface UserMissionCompleteDao extends BaseRepository<UserMissionComplete> {

    @Select("update t_u_user_mission_complete set invite_volume = #{inviteVolume} where id = #{id}")
    void update(BigDecimal inviteVolume, Long id);

    @Select("SELECT  " +
            "DATE_FORMAT(t1.gmt_create,'%Y-%m-%d') AS days, " +
            "SUM(IF(t1.activity_type='SIGN_IN', t1.volume * t2.amount, 0)) AS loginReward, " +
            "SUM(IF(t1.activity_type='WHATS_APP', t1.volume * t2.amount, 0)) AS shareWhatsapp, " +
            "SUM(IF(t1.activity_type='FACE_BOOK', t1.volume * t2.amount, 0)) AS shareFacebook, " +
            "SUM(IF(t1.activity_type='YOUTUBE', t1.volume * t2.amount, 0)) AS shareYoutube, " +
            "SUM(IF(t1.activity_type='TWITTER', t1.volume * t2.amount, 0)) AS shareTwitter, " +
            "SUM(IF(t1.activity_type='SUNSHINE', t1.volume * t2.amount, 0)) AS tgSunshine, " +
            "SUM(IF(t1.activity_type='JOIN_TG_GROUP', t1.volume * t2.amount, 0)) AS joinGroup, " +
            "SUM(IF(t1.activity_type='INVESTMENT', t1.volume * t2.amount, 0)) AS activityWager, " +
            "SUM(IF(t1.activity_type='INVITE' and t1.invite_audit_status='PASS', ((t1.invite_volume - t1.min_limit_volume) * t2.amount), 0)) AS inviteAmount " +
            "FROM t_u_user_mission_complete t1  " +
            "LEFT JOIN t_d_mission_activity t2 on t2.type = t1.activity_type  " +
            "WHERE `status`= 1 " +
            "GROUP BY days  HAVING days= #{localDate}")
    List<FundDataDTO> fundDataStatisticsByMissionComplete(@Param("localDate") LocalDate localDate);
}
