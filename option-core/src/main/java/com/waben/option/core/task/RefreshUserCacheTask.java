package com.waben.option.core.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.cache.LoadingCache;
import com.waben.option.common.model.dto.user.UserQueryDTO;
import com.waben.option.common.model.dto.user.UserStaDTO;
import com.waben.option.common.model.dto.user.UserTreeDTO;
import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.user.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class RefreshUserCacheTask {

//    @Resource
//    private LoadingCache<UserQueryDTO, Map<Long, UserTreeDTO>> loadingCache;
//
//    @Resource
//    private LoadingCache<UserQueryDTO, UserStaDTO> loadingCacheReulst;
//
//    @Resource
//    private UserDao userDao;
//
////    @Scheduled(cron = "0 0/5 * * * ?")
//    public void refreshUserCache() {
//        log.info("开始刷新用户层级缓存数据");
//        QueryWrapper<User> query = new QueryWrapper<>();
//        query.select(User.ID, User.PARENT_ID, User.GROUP_INDEX);
//        query.eq(User.AUTHORITY_TYPE, AuthorityEnum.CLIENT);
//        List<User> list = userDao.selectList(query);
//        for (User user : list) {
//            UserQueryDTO userQueryDTO = new UserQueryDTO();
//            userQueryDTO.setUserId(user.getId());
//            try {
//                userQueryDTO.setLevel(1);
//                loadingCache.get(userQueryDTO);
//            } catch (ExecutionException e) {
//                log.error("[" + JSON.toJSONString(userQueryDTO) + "]查询异常", e);
//            }
//            try {
//                userQueryDTO.setLevel(2);
//                loadingCache.get(userQueryDTO);
//            } catch (ExecutionException e) {
//                log.error("[" + JSON.toJSONString(userQueryDTO) + "]查询异常", e);
//            }
//            try {
//                userQueryDTO.setLevel(3);
//                loadingCache.get(userQueryDTO);
//            } catch (ExecutionException e) {
//                log.error("[" + JSON.toJSONString(userQueryDTO) + "]查询异常", e);
//            }
//        }
//    }
//
////    @Scheduled(cron = "0 0/5 * * * ?")
//    public void refreshUserStaCache() {
//        log.info("开始刷新用户统计缓存数据");
//        QueryWrapper<User> query = new QueryWrapper<>();
//        query.select(User.ID, User.PARENT_ID, User.GROUP_INDEX);
//        query.eq(User.AUTHORITY_TYPE, AuthorityEnum.CLIENT);
//        List<User> list = userDao.selectList(query);
//        for (User user : list) {
//            UserQueryDTO userQueryDTO = new UserQueryDTO();
//            userQueryDTO.setUserId(user.getId());
//            try {
//                userQueryDTO.setLevel(1);
//                loadingCacheReulst.get(userQueryDTO);
//            } catch (ExecutionException e) {
//                log.error("[" + JSON.toJSONString(userQueryDTO) + "]查询异常", e);
//            }
//            try {
//                userQueryDTO.setLevel(2);
//                loadingCacheReulst.get(userQueryDTO);
//            } catch (ExecutionException e) {
//                log.error("[" + JSON.toJSONString(userQueryDTO) + "]查询异常", e);
//            }
//            try {
//                userQueryDTO.setLevel(3);
//                loadingCacheReulst.get(userQueryDTO);
//            } catch (ExecutionException e) {
//                log.error("[" + JSON.toJSONString(userQueryDTO) + "]查询异常", e);
//            }
//        }
//    }

}
