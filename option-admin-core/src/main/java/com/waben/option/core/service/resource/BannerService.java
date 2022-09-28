package com.waben.option.core.service.resource;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.model.request.resource.CreateBannerRequest;
import com.waben.option.common.model.request.resource.UpdateBannerRequest;
import com.waben.option.data.entity.resource.Banner;
import com.waben.option.data.repository.resource.BannerDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BannerService {

    @Resource
    private BannerDao bannerDao;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private IdWorker idWorker;

    public BannerDTO createBanner(CreateBannerRequest request) {
        Banner banner = new Banner();
        banner.setId(idWorker.nextId());
        banner.setSeq(request.getSeq());
        banner.setEnable(request.getEnable());
        banner.setOperatorId(request.getOperatorId());
        banner.setDisplayType(request.getDisplayType());
        banner.setLanguageList(request.getLanguageList());
        bannerDao.insert(banner);
        return modelMapper.map(banner, BannerDTO.class);
    }

    public BannerDTO updateBanner(UpdateBannerRequest request) {
        Banner banner = bannerDao.selectById(request.getId());
        if (banner == null) throw new ServerException(1055);
        banner.setSeq(request.getSeq());
        banner.setEnable(request.getEnable());
        banner.setOperatorId(request.getOperatorId());
        banner.setDisplayType(request.getDisplayType());
        banner.setLanguageList(request.getLanguageList());
        bannerDao.updateById(banner);
        return modelMapper.map(banner, BannerDTO.class);
    }

    public void deleteBanner(Long bannerId) {
        Banner banner = bannerDao.selectById(bannerId);
        if (banner == null) {
            throw new ServerException(1055);
        }
        bannerDao.delete(new QueryWrapper<Banner>().eq(Banner.ID, bannerId));
    }

    public BannerDTO queryById(Long id) {
        return modelMapper.map(bannerDao.selectById(id), BannerDTO.class);
    }

    public List<BannerDTO> queryBannerList(Boolean enable, Integer displayType, int page, int size) {
        QueryWrapper<Banner> query = new QueryWrapper<>();
        if (enable != null) {
            query = query.eq(Banner.ENABLE, enable);
        }
        if (displayType != null) {
            query = query.eq(Banner.DISPLAY_TYPE, displayType);
        }
        query = query.orderByAsc(Banner.SEQ);
        IPage<Banner> pageWrapper = bannerDao.selectPage(new Page<>(page, size), query);
        if (pageWrapper.getTotal() > 0) {
            return pageWrapper.getRecords().stream().map(banner -> modelMapper.map(banner, BannerDTO.class))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
