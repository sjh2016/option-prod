package com.waben.option.controller.summary;

import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.summary.UserFissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/userFission")
@Api(tags = {"用户裂变数据"})
public class UserFissionController extends AbstractBaseController {

    @Resource
    private UserFissionService userFissionService;

    @ApiOperation(value = "查询裂变数据列表", response = BannerDTO.class)
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "mobilePhone", required = false) String mobilePhone,
                                       @ApiParam(name = "page", value = "页码") int page,
                                       @ApiParam(name = "size", value = "每页数量") int size) {
        return ok(userFissionService.queryList(mobilePhone, page, size));
    }

}
