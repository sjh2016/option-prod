package com.waben.option.service.resource;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waben.option.common.interfacesadmin.resource.AdminLuckyDrawAPI;
import com.waben.option.common.model.dto.resource.LuckyDrawCommodityDTO;

@Service
public class LuckyDrawService {

    @Resource
    private AdminLuckyDrawAPI adminLuckyDrawAPI;

    public BigDecimal luckyDraw(Long userId) {
        return adminLuckyDrawAPI.luckyDraw(userId);
    }

   public List<LuckyDrawCommodityDTO> queryLuckyDrawCommodity(){
        return adminLuckyDrawAPI.queryLuckyDrawCommodity();
   }

}
