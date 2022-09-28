package com.waben.option.service.resource;

import com.waben.option.common.interfaces.resource.LuckyDrawAPI;
import com.waben.option.common.model.dto.resource.LuckyDrawCommodityDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
public class LuckyDrawService {

    @Resource
    private LuckyDrawAPI luckyDrawAPI;

    public BigDecimal luckyDraw(Long userId) {
        return luckyDrawAPI.luckyDraw(userId);
    }

   public List<LuckyDrawCommodityDTO> queryLuckyDrawCommodity(){
        return luckyDrawAPI.queryLuckyDrawCommodity();
   }

}
