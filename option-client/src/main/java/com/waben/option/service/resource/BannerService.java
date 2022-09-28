package com.waben.option.service.resource;

import com.waben.option.common.component.LocaleContext;
import com.waben.option.common.interfaces.resource.BannerAPI;
import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.model.request.resource.CreateBannerRequest;
import com.waben.option.common.model.request.resource.UpdateBannerRequest;
import com.waben.option.mode.vo.BannerVO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Service
public class BannerService {

    @Resource
    private BannerAPI bannerAPI;

    @Resource
    private ModelMapper modelMapper;

    public BannerDTO createBanner(CreateBannerRequest request) {
        return bannerAPI.createBanner(request);
    }

    public BannerDTO updateBanner(UpdateBannerRequest request) {
        return bannerAPI.updateBanner(request);
    }

    public void deleteBanner(Long bannerId) {
        bannerAPI.deleteBanner(bannerId);
    }

    public BannerDTO queryBanner(Long id) {
        return bannerAPI.queryBanner(id);
    }

    public List<BannerDTO> queryBannerList(Boolean enable, Integer displayType, int page, int size) {
        return bannerAPI.queryBannerList(enable, displayType, page, size);
    }

    public List<BannerVO> queryBannerList() {
        String locale = LocaleContext.get();
        List<BannerVO> voList = new ArrayList<>();
        List<BannerDTO> bannerList = bannerAPI.queryBannerList(true, 1, 1, 10);
        for (BannerDTO bannerDTO : bannerList) {
            BannerVO vo = modelMapper.map(bannerDTO, BannerVO.class);
            if (!CollectionUtils.isEmpty(bannerDTO.getLanguageList())) {
                for (BannerDTO.BannerLanguageDTO languageDTO : bannerDTO.getLanguageList()) {
                    if (locale.equals(languageDTO.getLanguage())) {
                        if (vo == null) vo = new BannerVO();
                        vo.setLanguage(locale);
                        vo.setDescription(languageDTO.getDescription());
                        vo.setImgDarkUrl(languageDTO.getImgDarkUrl());
                        vo.setImgLightUrl(languageDTO.getImgLightUrl());
                        vo.setSkipUrl(languageDTO.getSkipUrl());
                        break;
                    }
                }
                voList.add(vo);
            }
        }
        return voList;
    }
}
