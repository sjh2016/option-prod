package com.waben.option.core.service.statement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.SpringContext;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.account.UserAccountStatementDTO;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.enums.InviteAuditStatusEnum;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.common.model.query.UserAccountStatementQuery;
import com.waben.option.data.entity.activity.ActivityUserJoin;
import com.waben.option.data.entity.statement.*;
import com.waben.option.data.entity.user.AccountStatement;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.repository.BaseRepository;
import com.waben.option.data.repository.activity.ActivityUserJoinDao;
import com.waben.option.data.repository.statement.AccountStatementStaDao;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AccountStatementService {

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private ActivityUserJoinDao activityUserJoinDao;

    public BaseRepository getRepo(Integer groupIndex) {
        return (BaseRepository) SpringContext.getBean("accountStatementDao" + groupIndex);
    }

    public Object getEntity(AccountStatement statement, Integer groupIndex) {
        if (groupIndex.intValue() == 1) {
            return modelMapper.map(statement, AccountStatement1.class);
        } else if (groupIndex.intValue() == 2) {
            return modelMapper.map(statement, AccountStatement2.class);
        } else if (groupIndex.intValue() == 3) {
            return modelMapper.map(statement, AccountStatement3.class);
        } else if (groupIndex.intValue() == 4) {
            return modelMapper.map(statement, AccountStatement4.class);
        } else if (groupIndex.intValue() == 5) {
            return modelMapper.map(statement, AccountStatement5.class);
        } else if (groupIndex.intValue() == 6) {
            return modelMapper.map(statement, AccountStatement6.class);
        } else if (groupIndex.intValue() == 7) {
            return modelMapper.map(statement, AccountStatement7.class);
        } else if (groupIndex.intValue() == 8) {
            return modelMapper.map(statement, AccountStatement8.class);
        } else if (groupIndex.intValue() == 9) {
            return modelMapper.map(statement, AccountStatement9.class);
        } else if (groupIndex.intValue() == 10) {
            return modelMapper.map(statement, AccountStatement10.class);
        } else if (groupIndex.intValue() == 11) {
            return modelMapper.map(statement, AccountStatement11.class);
        } else if (groupIndex.intValue() == 12) {
            return modelMapper.map(statement, AccountStatement12.class);
        } else if (groupIndex.intValue() == 13) {
            return modelMapper.map(statement, AccountStatement13.class);
        } else if (groupIndex.intValue() == 14) {
            return modelMapper.map(statement, AccountStatement14.class);
        } else if (groupIndex.intValue() == 15) {
            return modelMapper.map(statement, AccountStatement15.class);
        } else if (groupIndex.intValue() == 16) {
            return modelMapper.map(statement, AccountStatement16.class);
        } else if (groupIndex.intValue() == 17) {
            return modelMapper.map(statement, AccountStatement17.class);
        } else if (groupIndex.intValue() == 18) {
            return modelMapper.map(statement, AccountStatement18.class);
        } else if (groupIndex.intValue() == 19) {
            return modelMapper.map(statement, AccountStatement19.class);
        } else if (groupIndex.intValue() == 20) {
            return modelMapper.map(statement, AccountStatement20.class);
        }
        return null;
    }

    public PageInfo<UserAccountStatementDTO> selectPage(User user, UserAccountStatementQuery query) {
        BaseRepository repo = (BaseRepository) SpringContext.getBean("accountStatementDao" + user.getGroupIndex());
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(AccountStatement.USER_ID, user.getId());
        if (query.getStartTime() != null) {
            queryWrapper.ge(AccountStatement.GMT_CREATE, query.getStartTime());
        }
        if (query.getEndTime() != null) {
            queryWrapper.le(AccountStatement.GMT_CREATE, query.getEndTime());
        }
        if (query.getType() != null && query.getType().length > 0) {
            queryWrapper.in(AccountStatement.TYPE, query.getType());
        }
        queryWrapper.orderByDesc(AccountStatement.GMT_CREATE);
        queryWrapper.orderByDesc(AccountStatement.ID);
        IPage pageData = repo.selectPage(new Page<>(query.getPage(), query.getSize()), queryWrapper);
        PageInfo<UserAccountStatementDTO> pageInfo = new PageInfo<>();
        if (pageData.getTotal() > 0) {
            List<UserAccountStatementDTO> statementList = new ArrayList<>();
            for (Object object : pageData.getRecords()) {
                UserAccountStatementDTO userAccountStatementDTO = modelMapper.map(object,
                        UserAccountStatementDTO.class);
                userAccountStatementDTO.setUsername(user.getUsername());
                userAccountStatementDTO.setAmount(userAccountStatementDTO.getAmount());
                //判断是否是邀请奖励
                if (TransactionEnum.CREDIT_INVITE_REGISTER == userAccountStatementDTO.getType()) {
                    QueryWrapper joinQueryWrapper = new QueryWrapper();
                    joinQueryWrapper.eq(ActivityUserJoin.USER_ID, userAccountStatementDTO.getUserId());
                    joinQueryWrapper.eq(ActivityUserJoin.ACTIVITY_TYPE, ActivityTypeEnum.INVITE);
                    joinQueryWrapper.eq(ActivityUserJoin.STATEMENT_ID, userAccountStatementDTO.getId());
                    joinQueryWrapper.eq(ActivityUserJoin.INVITE_AUDIT_STATUS, InviteAuditStatusEnum.PASS);
                    Integer count = activityUserJoinDao.selectCount(joinQueryWrapper);
                    log.info("2用户{}获取到{}个邀请信息", userAccountStatementDTO.getUserId(), count);
                    if (null != count && count > 0) {
                        statementList.add(userAccountStatementDTO);
                    }
                } else {
                    statementList.add(userAccountStatementDTO);
                }
            }
            pageInfo.setRecords(statementList);
            pageInfo.setTotal(pageData.getTotal());
            pageInfo.setPage((int) pageData.getPages());
            pageInfo.setSize((int) pageData.getSize());
            return pageInfo;
        }
        return pageInfo;
    }

//    public BigDecimal queryChidAmount(long userId, UserAccountStatementQuery query, int level) {
//        BigDecimal bigDecimal = BigDecimal.ZERO;
//        BigDecimal childAmount = BigDecimal.ZERO;
//        if (level > query.getLevel()) {
//            return bigDecimal;
//        }
//        if (level > LIMIT_USER_LEVEL) {
//            return bigDecimal;
//        }
//        List<User> children = userDao.selectList(new QueryWrapper<User>().eq(User.PARENT_ID, userId));
//        if (children != null && children.size() > 0) {
//            for (User child : children) {
//                BaseRepository repo = (BaseRepository) SpringContext.getBean("accountStatementDao" + child.getGroupIndex());
//                QueryWrapper queryWrapper = new QueryWrapper();
//                if (query.getStartTime() != null) {
//                    queryWrapper.ge(AccountStatement.GMT_CREATE, query.getStartTime());
//                }
//                if (query.getEndTime() != null) {
//                    queryWrapper.le(AccountStatement.GMT_CREATE, query.getEndTime());
//                }
//                if (query.getType() != null && query.getType().length > 0) {
//                    queryWrapper.in(AccountStatement.TYPE, query.getType());
//                }
//                queryWrapper.eq(AccountStatement.USER_ID, child.getId());
//                queryWrapper.orderByDesc(AccountStatement.GMT_CREATE);
//                queryWrapper.orderByDesc(AccountStatement.ID);
//                List<Object> list = repo.selectList(queryWrapper);
//                if (null != list && !list.isEmpty()) {
//                    for (Object object : list) {
//                        UserAccountStatementDTO statementDTO = modelMapper.map(object, UserAccountStatementDTO.class);
//                        bigDecimal.add(statementDTO.getAmount());
//                        childAmount.add(queryChidAmount(child.getId(), query, level + 1));
//                    }
//                }
//            }
//        }
//        //一级分销金额计算
//        if (1 == level) {
//            bigDecimal = bigDecimal.multiply(new BigDecimal(TEAM_LEVEL_1_EARNINGS));
//        }
//        //二级分销金额计算
//        if (2 == level) {
//            bigDecimal = bigDecimal.multiply(new BigDecimal(TEAM_LEVEL_2_EARNINGS));
//        }
//        //三级分销金额计算
//        if (3 == level) {
//            bigDecimal = bigDecimal.multiply(new BigDecimal(TEAM_LEVEL_3_EARNINGS));
//        }
//        bigDecimal.add(childAmount);
//        return bigDecimal;
//    }

    public List<UserAccountStatementDTO> selectList(QueryWrapper queryWrapper, Integer groupIndex) {
        List<UserAccountStatementDTO> result = new ArrayList<>();
        BaseRepository repo = (BaseRepository) SpringContext.getBean("accountStatementDao" + groupIndex);
        List list = repo.selectList(queryWrapper);
        if (list != null && list.size() > 0) {
            for (Object object : list) {
                result.add(modelMapper.map(object, UserAccountStatementDTO.class));
            }
        }
        return result;
    }

    public BigDecimal querySta(Long userId, Integer groupIndex, int level) {
        AccountStatementStaDao repo = (AccountStatementStaDao) SpringContext.getBean("accountStatementDao" + groupIndex);
        return repo.querySta(userId, level);
    }

    public Integer selectCount(QueryWrapper queryWrapper, Integer groupIndex) {
        BaseRepository repo = (BaseRepository) SpringContext.getBean("accountStatementDao" + groupIndex);
        Integer result = repo.selectCount(queryWrapper);
        return result != null ? result : 0;
    }

}
