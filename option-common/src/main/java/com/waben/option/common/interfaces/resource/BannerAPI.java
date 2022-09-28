package com.waben.option.common.interfaces.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.model.request.resource.CreateBannerRequest;
import com.waben.option.common.model.request.resource.UpdateBannerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "core-server", contextId = "BannerAPI", qualifier = "bannerAPI")
public interface BannerAPI extends BaseAPI {

    @RequestMapping(value = "/banner/query", method = RequestMethod.GET)
    public Response<BannerDTO> _queryBanner(@RequestParam("id") Long id);

    @RequestMapping(value = "/banner/list", method = RequestMethod.GET)
    public Response<List<BannerDTO>> _queryBannerList(@RequestParam("enable") Boolean enable,
                                                      @RequestParam(value = "displayType", required = false) Integer displayType,
                                                      @RequestParam("page") int page, @RequestParam("size") int size);

    @RequestMapping(value = "/banner/create", method = RequestMethod.POST)
    public Response<BannerDTO> _createBanner(@RequestBody CreateBannerRequest request);

    @RequestMapping(value = "/banner/update", method = RequestMethod.POST)
    public Response<BannerDTO> _updateBanner(@RequestBody UpdateBannerRequest request);

    @RequestMapping(value = "/banner/delete", method = RequestMethod.DELETE)
    public Response<Void> _deleteBanner(@RequestParam("id") Long id);

    /**
     * 查询轮播图
     *
     * @param id 轮播图ID
     * @return
     */
    public default BannerDTO queryBanner(Long id) {
        Response<BannerDTO> response = _queryBanner(id);
        return getResponseData(response);
    }

    /**
     * 查询轮播图列表
     *
     * @param enable 是否可用
     * @param page   页码
     * @param size   每页数量
     * @return
     */
    public default List<BannerDTO> queryBannerList(Boolean enable, Integer displayType, int page, int size) {
        Response<List<BannerDTO>> response = _queryBannerList(enable, displayType, page, size);
        return getResponseData(response);
    }

    /**
     * 创建轮播图
     *
     * @param request
     * @return
     */
    public default BannerDTO createBanner(CreateBannerRequest request) {
        Response<BannerDTO> response = _createBanner(request);
        return getResponseData(response);
    }

    /**
     * 修改轮播图
     *
     * @param request
     * @return
     */
    public default BannerDTO updateBanner(UpdateBannerRequest request) {
        Response<BannerDTO> response = _updateBanner(request);
        return getResponseData(response);
    }

    /**
     * 删除轮播图
     *
     * @param id 轮播图id
     */
    public default void deleteBanner(Long id) {
        Response<Void> response = _deleteBanner(id);
        getResponseData(response);
    }
}
