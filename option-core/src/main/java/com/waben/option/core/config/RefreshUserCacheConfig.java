package com.waben.option.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class RefreshUserCacheConfig {

//    @Bean("loadingCache")
//    @DependsOn("userService")
//    public LoadingCache<UserQueryDTO, Map<Long, UserTreeDTO>> loadingCache(UserService userService) {
//        return CacheBuilder.newBuilder().refreshAfterWrite(10,
//                TimeUnit.MINUTES).build(new CacheLoader<UserQueryDTO, Map<Long, UserTreeDTO>>() {
//            @Override
//            public Map<Long, UserTreeDTO> load(UserQueryDTO userQueryDTO) throws Exception {
//                return userService.userTreeMap(userQueryDTO);
//            }
//        });
//    }

//    @Bean("loadingCacheReulst")
//    @DependsOn("userService")
//    public LoadingCache<UserQueryDTO, UserStaDTO> loadingCacheReulst(UserService userService) {
//        return CacheBuilder.newBuilder().refreshAfterWrite(10,
//                TimeUnit.MINUTES).build(new CacheLoader<UserQueryDTO, UserStaDTO>() {
//            @Override
//            public UserStaDTO load(UserQueryDTO userQueryDTO) throws Exception {
//                return userService.staLevel(userQueryDTO);
//            }
//        });
//    }

}
