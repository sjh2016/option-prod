package com.waben.option.service.resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.waben.option.common.component.IdWorker;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.resource.ConfigAPI;
import com.waben.option.common.interfaces.resource.SunshineAPI;
import com.waben.option.common.interfaces.user.UserAPI;
import com.waben.option.common.model.enums.SunshineTypeEnum;
import com.waben.option.common.model.request.resource.SunshineRequest;
import com.waben.option.common.model.request.user.UpdateUserBasicRequest;
import com.waben.option.common.util.AmazonUtil;

@Service
public class UploadService {

	private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");

	private AtomicInteger seq = new AtomicInteger();

	@Resource
	private IdWorker idWorker;

	@Resource
	private ConfigAPI configAPI;

	@Resource
	private SunshineAPI sunshineAPI;

	@Resource
	private UserAPI userAPI;

	public String uploadFileSystem(MultipartFile mFile) {
		String uploadFilePath = configAPI.queryUploadPathConfig();
		String fileName = mFile.getOriginalFilename();
		int index = fileName.lastIndexOf(".");
		String fileSuffix = fileName.substring(index);
		if (!StringUtils.isBlank(fileName.trim())) {
			LocalDateTime dateTime = LocalDateTime.now();
			String[] timeStrs = dateTime.format(df).split(" ");
			String dirPath = uploadFilePath;
			String resoultPath = "/" + timeStrs[0] + "/" + timeStrs[1] + String.format("%03d", seq.incrementAndGet())
					+ fileSuffix;
			String path = dirPath + resoultPath;
			File dir = new File(dirPath + "/" + timeStrs[0]);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File localFile = new File(path);
			try {
				mFile.transferTo(localFile);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServerException(1010);
			}
			return resoultPath;
		}
		throw new ServerException(1010);
	}

	public String userUpload(MultipartFile file, Long userId) {
		String uploadFilePath = configAPI.queryUploadPathConfig();
		String fileName = file.getOriginalFilename();
		int index = fileName.lastIndexOf(".");
		String fileSuffix = fileName.substring(index);
		if (!StringUtils.isBlank(fileName.trim())) {
			LocalDateTime dateTime = LocalDateTime.now();
			String[] timeStrs = dateTime.format(df).split(" ");
			String dirPath = uploadFilePath;
			String resoultPath = "/" + timeStrs[0] + "/" + timeStrs[1]
					+ String.format("%03d", seq.incrementAndGet()) + fileSuffix;
			String path = dirPath + resoultPath;
			File dir = new File(dirPath + "/" + timeStrs[0]);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File localFile = new File(path);
			try {
				file.transferTo(localFile);
			} catch (Exception e) {
				throw new ServerException(1010);
			}
			UpdateUserBasicRequest request = new UpdateUserBasicRequest();
			request.setUserId(userId);
			request.setHeadImg(resoultPath);
			userAPI.updateUserBasic(request);
			return resoultPath;
		}
		throw new ServerException(1010);
	}

	public String queryUrlPath() {
		return configAPI.queryImageUrlConfig();
	}

	public String sunshineFileSystem(MultipartFile mFile, String folder, Long userId, SunshineTypeEnum type) {
		String uploadFilePath = configAPI.queryUploadPathConfig();
		String fileName = mFile.getOriginalFilename();
		int index = fileName.lastIndexOf(".");
		String fileSuffix = fileName.substring(index);
		if (!StringUtils.isBlank(fileName.trim())) {
			LocalDateTime dateTime = LocalDateTime.now();
			String[] timeStrs = dateTime.format(df).split(" ");
			String dirPath = uploadFilePath;
			String resoultPath = "/" + timeStrs[0] + "/" + timeStrs[1] + String.format("%03d", seq.incrementAndGet())
					+ fileSuffix;
			String path = dirPath + resoultPath;
			File dir = new File(dirPath + "/" + timeStrs[0]);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File localFile = new File(path);
			try {
				mFile.transferTo(localFile);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServerException(1010);
			}

			sunshineAPI.upload(userId, type, resoultPath);
			return resoultPath;
		}
		throw new ServerException(1010);
	}

	public String sunshineS3(MultipartFile mFile, String folder, Long userId, SunshineTypeEnum type) {
		String fileName = s3(mFile, folder);
		SunshineRequest request = new SunshineRequest();
		request.setType(type);
		request.setUrl(fileName);
		request.setUserId(userId);
		sunshineAPI.createOrUpdate(request);
		return fileName;
	}

	public String s3(MultipartFile mFile, String folder) {
		File file = new File(mFile.getOriginalFilename());
		try {
			if (org.springframework.util.StringUtils.isEmpty(folder))
				folder = "file";
			FileUtils.copyInputStreamToFile(mFile.getInputStream(), file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		URL url = AmazonUtil.uploadS3(file, folder);
		// 会在本地产生临时文件，用完后需要删除
		if (file.exists()) {
			file.delete();
		}
		String urlPage = url.getPath();
		String fileName = urlPage.substring(15);
		return fileName;
	}

	public String getS3Token(String folder) {
		URL url = AmazonUtil.getAmazonS3Token(folder);
		return url.getQuery();
	}

}
