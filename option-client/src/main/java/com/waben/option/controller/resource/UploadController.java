package com.waben.option.controller.resource;

import com.waben.option.common.model.enums.SunshineTypeEnum;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.resource.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/upload")
@Api(tags = "文件上传接口")
public class UploadController extends AbstractBaseController {

    @Resource
    private UploadService uploadService;

    @ApiOperation(value = "上传文件")
    @RequestMapping(value = "/img", method = RequestMethod.POST)
    public ResponseEntity<?> uploadBanner(@RequestParam("file") MultipartFile file) {
        return ok(uploadService.userUpload(file, getCurrentUserId()));
    }

    @ApiOperation(value = "查询图片url前缀")
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    public ResponseEntity<?> queryUrlPath() {
        return ok(uploadService.queryUrlPath());
    }

    @ApiOperation(value = "上传文件图片到本地")
    @RequestMapping(value = "/fs", method = RequestMethod.POST)
    public ResponseEntity<?> s3(@RequestParam("file") MultipartFile file, @RequestParam(value = "folder", required = false) String folder) {
        // return ok(uploadService.s3(file, folder));
    	return ok(uploadService.uploadFileSystem(file));
    }

    @ApiOperation(value = "获取S3 Token")
    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public ResponseEntity<?> getS3Token(@RequestParam("folder") String folder) {
        return ok(uploadService.getS3Token(folder));
    }

    @ApiOperation(value = "上传分享图片")
    @RequestMapping(value = "/sunshine", method = RequestMethod.POST)
    public ResponseEntity<?> sunshine(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "folder", required = false) String folder, @RequestParam(value = "type", required = false) SunshineTypeEnum type) {
        return ok(uploadService.sunshineFileSystem(file, folder, getCurrentUserId(), type));
    }
}
