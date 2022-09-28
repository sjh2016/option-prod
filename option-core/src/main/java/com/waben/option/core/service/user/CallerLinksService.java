package com.waben.option.core.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.model.dto.user.CallerLinksDTO;
import com.waben.option.common.model.request.common.IdRequest;
import com.waben.option.common.model.request.user.CallerLinksRequest;
import com.waben.option.data.entity.user.CallerLinks;
import com.waben.option.data.repository.user.CallerLinksDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Peter
 * @date: 2021/7/8 13:58
 */
@Service
public class CallerLinksService {

    @Resource
    private CallerLinksDao callerLinksDao;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private IdWorker idWorker;

    public List<CallerLinksDTO> query(Long id, String type, String name, Boolean enable) {
        QueryWrapper<CallerLinks> query = new QueryWrapper<>();
        if (id != null) {
            query = query.eq(CallerLinks.ID, id);
        }
        if (!StringUtils.isEmpty(type)) {
            query = query.eq(CallerLinks.TYPE, type);
        }
        if (!StringUtils.isEmpty(name)) {
            query = query.eq(CallerLinks.NAME, name);
        }
        if (enable != null) {
            query = query.eq(CallerLinks.ENABLE, enable);
        }
        query = query.orderByDesc(CallerLinks.TYPE).orderByDesc(CallerLinks.ENABLE);
        List<CallerLinks> linksList = callerLinksDao.selectList(query);
        if (!CollectionUtils.isEmpty(linksList)) {
            return linksList.stream().map(links ->
                    modelMapper.map(links, CallerLinksDTO.class)).collect(Collectors.toList());
        }
        return null;
    }


    public void modify(CallerLinksRequest request) {
        CallerLinks callerLinks = callerLinksDao.selectById(request.getId());
        if (callerLinks != null) {
            callerLinks.setEnable(!callerLinks.getEnable());
            callerLinks.setLink(request.getLink());
            callerLinks.setName(request.getName());
            callerLinks.setType(request.getType());
            callerLinks.setEnable(request.getEnable());
            callerLinksDao.updateById(callerLinks);
        }
    }

    public void create(CallerLinksRequest request) {
        CallerLinks callerLinks = new CallerLinks();
        callerLinks.setId(idWorker.nextId());
        callerLinks.setType(request.getType());
        callerLinks.setName(request.getName());
        callerLinks.setLink(request.getLink());
        callerLinks.setEnable(request.getEnable() != null && request.getEnable());
        callerLinksDao.insert(callerLinks);
    }

    public void delete(IdRequest request) {
        callerLinksDao.deleteById(request.getId());
    }
}
