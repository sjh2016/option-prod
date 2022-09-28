package com.waben.option.core.service.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.model.dto.resource.LeaderboardDTO;
import com.waben.option.common.model.enums.LeaderboardTypeEnum;
import com.waben.option.common.model.request.resource.LeaderboardRequest;
import com.waben.option.data.entity.resource.Leaderboard;
import com.waben.option.data.repository.resource.LeaderboardDao;

/**
 * 排行榜
 *
 * @author: Peter
 * @date: 2021/6/23 15:57
 */
@Service
public class LeaderboardService {

    @Resource
    private IdWorker idWorker;

    @Resource
    private LeaderboardDao leaderboardDao;

    @Resource
    private ModelMapper modelMapper;

//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public void create(LeaderboardRequest request) {
        Leaderboard leaderboard = modelMapper.map(request, Leaderboard.class);
        leaderboard.setId(idWorker.nextId());
        leaderboardDao.insert(leaderboard);
    }

    public List<LeaderboardDTO> query(LeaderboardTypeEnum type, int page, int size) {
        QueryWrapper<Leaderboard> queryWrapper = new QueryWrapper<>();
        if (type != null){
            queryWrapper = queryWrapper.eq(Leaderboard.TYPE, type.name());
        }
        queryWrapper.orderByDesc(Leaderboard.POWER);
        IPage<Leaderboard> iPage = leaderboardDao.selectPage(new Page<>(page, size), queryWrapper);
        return iPage.getRecords().stream().map(leaderboard -> modelMapper.map(leaderboard, LeaderboardDTO.class)).collect(Collectors.toList());
    }
}
