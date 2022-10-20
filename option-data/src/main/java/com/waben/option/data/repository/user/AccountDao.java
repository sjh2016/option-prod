package com.waben.option.data.repository.user;

import com.waben.option.data.entity.user.Account;
import com.waben.option.data.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/6/23 9:15
 */
@Mapper
public interface AccountDao extends BaseRepository<Account> {

    @Select("UPDATE t_u_account SET balance = #{balance}, freeze_capital = #{freezeCapital} WHERE id = #{id}")
    void update(Long id, BigDecimal balance, BigDecimal freezeCapital);

    @Select("SELECT * FROM t_u_account where id = #{id}")
    Account selectById(Long id);

    @Select({"<script>", "<if test=\"level==1\">\n" +
            "SELECT commission FROM t_u_account WHERE user_id=#{userId}\n" +
            "</if>\n" +
            "<if test=\"level==2\">\n" +
            "SELECT second_commission FROM t_u_account WHERE user_id=#{userId}\n" +
            "</if>\n" +
            "<if test=\"level==3\">\n" +
            "SELECT thrid_commission FROM t_u_account WHERE user_id=#{userId}\n" +
            "</if>", "</script>"})
    BigDecimal queryLevel(@Param("userId") Long userId, @Param("level") int level);

    @Select({"<script>", "SELECT (commission+second_commission+thrid_commission) AS amount FROM t_u_account WHERE " +
            "user_id=#{userId}", "</script>"})
    BigDecimal queryLevelCount(@Param("userId") Long userId);



}
