package com.waben.option.core.service.resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.CountryDTO;
import com.waben.option.common.model.dto.user.logger.UserLoggerDTO;
import com.waben.option.data.entity.resource.Country;
import com.waben.option.data.entity.user.UserLogger;
import com.waben.option.data.repository.resource.CountryDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryService {

    @Resource
    private CountryDao countryDao;

    @Resource
    private ModelMapper modelMapper;

    public PageInfo<CountryDTO> queryList(String countryEn,int page,int size) {
        QueryWrapper<Country> query = new QueryWrapper<>();
        if (countryEn != null) {
            query = query.eq(Country.COUNTRY_EN,countryEn);
        }
        query.orderByAsc(Country.COUNTRY_EN);
        PageInfo<CountryDTO> pageInfo = new PageInfo<>();
        IPage<Country> countryIPage = countryDao.selectPage(new Page<>(page,size), query);
        if (countryIPage.getTotal() > 0) {
            List<CountryDTO> countryList = countryIPage.getRecords().stream().map(country -> modelMapper.map(country, CountryDTO.class))
                    .collect(Collectors.toList());
            pageInfo.setRecords(countryList);
            pageInfo.setTotal(countryIPage.getTotal());
            pageInfo.setPage((int) countryIPage.getPages());
            pageInfo.setSize((int) countryIPage.getSize());
            return pageInfo;
        }
        return pageInfo;
    }

}
