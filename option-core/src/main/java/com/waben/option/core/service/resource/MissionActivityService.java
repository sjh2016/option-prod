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
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.MissionActivityDTO;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.request.resource.MissionActivityRequest;
import com.waben.option.data.entity.resource.IncomeRank;
import com.waben.option.data.entity.resource.MissionActivity;
import com.waben.option.data.repository.resource.MissionActivityDao;

@Service
public class MissionActivityService {

    @Resource
    private MissionActivityDao missionActivityDao;

    @Resource
    private IdWorker idWorker;

    @Resource
    private ModelMapper modelMapper;

    public PageInfo<MissionActivityDTO> queryList(String type, int page, int size) {
        QueryWrapper<MissionActivity> query = new QueryWrapper<>();
        if (type != null) {
            query = query.eq(IncomeRank.TYPE, type);
        }
        query.orderByAsc(MissionActivity.SORT);
        PageInfo<MissionActivityDTO> pageInfo = new PageInfo<>();
        IPage<MissionActivity> missionActivityPage = missionActivityDao.selectPage(new Page<>(page, size), query);
        if (missionActivityPage.getTotal() > 0) {
            List<MissionActivityDTO> missionActivityList = missionActivityPage.getRecords().stream().map(missionActivity -> modelMapper.map(missionActivity, MissionActivityDTO.class))
                    .collect(Collectors.toList());
            pageInfo.setRecords(missionActivityList);
            pageInfo.setTotal(missionActivityPage.getTotal());
            pageInfo.setPage((int) missionActivityPage.getPages());
            pageInfo.setSize((int) missionActivityPage.getSize());
            return pageInfo;
        }
        return pageInfo;
    }

//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public MissionActivityDTO create(MissionActivityRequest request) {
        MissionActivity missionActivity = new MissionActivity();
        missionActivity.setId(idWorker.nextId());
        missionActivity.setAmount(request.getAmount());
        missionActivity.setName(request.getName());
        missionActivity.setType(request.getType());
        missionActivity.setDescription(request.getDescription());
        missionActivity.setEnable(request.getEnable());
        missionActivity.setSort(request.getSort());
        missionActivity.setMinLimitNumber(request.getMinLimitNumber());
        missionActivity.setMaxLimitNumber(request.getMaxLimitNumber());
        missionActivityDao.insert(missionActivity);
        return modelMapper.map(missionActivity, MissionActivityDTO.class);
    }

//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public MissionActivityDTO upset(MissionActivityRequest request) {
        MissionActivity missionActivity = missionActivityDao.selectById(request.getId());
        if (missionActivity == null) throw new ServerException(1016);
        missionActivity.setAmount(request.getAmount());
        missionActivity.setMinLimitNumber(request.getMinLimitNumber());
        missionActivity.setMaxLimitNumber(request.getMaxLimitNumber());
        missionActivity.setType(request.getType());
        missionActivity.setEnable(request.getEnable());
        missionActivityDao.updateById(missionActivity);
        return modelMapper.map(missionActivity, MissionActivityDTO.class);
    }

//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        MissionActivity missionActivity = missionActivityDao.selectById(id);
        if (missionActivity == null) throw new ServerException(1016);
        missionActivityDao.deleteById(id);
    }

    public MissionActivityDTO queryByType(ActivityTypeEnum type) {
        MissionActivity activity = missionActivityDao.selectOne(new QueryWrapper<MissionActivity>().eq(MissionActivity.TYPE, type));
        if (activity.getEnable()) return modelMapper.map(activity, MissionActivityDTO.class);
        throw new ServerException(5009); // 任务已失效
    }

}
