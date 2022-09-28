package com.waben.option.core.service.resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.account.UserAccountDTO;
import com.waben.option.common.model.dto.resource.PaymentImageDTO;
import com.waben.option.common.model.enums.SunshineTypeEnum;
import com.waben.option.common.model.request.resource.PaymentImageRequest;
import com.waben.option.data.entity.resource.PaymentImage;
import com.waben.option.data.repository.resource.PaymentImageDao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentImageService {

    @Resource
    private PaymentImageDao paymentImageDao;

    @Resource
    private IdWorker idWorker;

    @Resource
    private ModelMapper modelMapper;

    public PageInfo<PaymentImageDTO> queryList(LocalDate day) {
        QueryWrapper<PaymentImage> query = new QueryWrapper<>();
        if (day != null) {
            query = query.eq(PaymentImage.DAY,day);
        }
        query.orderByDesc(PaymentImage.DAY);
        PageInfo<PaymentImageDTO> pageInfo = new PageInfo<>();
        IPage<PaymentImage> paymentImageIPage = paymentImageDao.selectPage(new Page<>(1,100), query);
        if (paymentImageIPage.getTotal() > 0) {
            List<PaymentImageDTO> paymentImageList = paymentImageIPage.getRecords().stream().map(paymentImage -> modelMapper.map(paymentImage, PaymentImageDTO.class))
                    .collect(Collectors.toList());
            pageInfo.setRecords(paymentImageList);
            pageInfo.setTotal(paymentImageIPage.getTotal());
            pageInfo.setPage((int) paymentImageIPage.getPages());
            pageInfo.setSize((int) paymentImageIPage.getSize());
            return pageInfo;
        }
        return pageInfo;
    }

    public void createToUpset(PaymentImageRequest request) {
        PaymentImage paymentImage = new PaymentImage();
        if (request.getId() != null) {
            paymentImage = paymentImageDao.selectById(request.getId());
            if (paymentImage != null) {
                paymentImage.setImages(request.getImages());
                paymentImage.setTitle(request.getTitle());
                paymentImage.setType(request.getType());
                paymentImage.setDay(request.getDay());
                paymentImageDao.updateById(paymentImage);
            }
        } else {
            paymentImage.setId(idWorker.nextId());
            paymentImage.setDay(request.getDay());
            paymentImage.setImages(request.getImages());
            paymentImage.setType(request.getType());
            paymentImage.setTitle(request.getTitle());
            paymentImageDao.insert(paymentImage);
        }
    }

    public List<PaymentImageDTO> query() {
        List<PaymentImage> paymentImageList = paymentImageDao.selectList(new QueryWrapper<PaymentImage>().orderByDesc(PaymentImage.DAY));
        return paymentImageList.stream().map(paymentImage ->
                modelMapper.map(paymentImage, PaymentImageDTO.class)).collect(Collectors.toList());
    }

}
