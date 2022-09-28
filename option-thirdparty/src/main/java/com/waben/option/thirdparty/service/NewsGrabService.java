package com.waben.option.thirdparty.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.waben.option.common.interfaces.resource.NewsAPI;
import com.waben.option.common.model.dto.resource.NewsDTO;
import com.waben.option.common.model.enums.NewsTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class NewsGrabService {

	public abstract NewsTypeEnum supportedNewsType();

	public abstract String retrieveUrl();

	public abstract List<NewsDTO> retrieveData(String url) throws Exception;

	@Resource
	protected NewsAPI newsApi;

	public void grab(String url) {
		NewsTypeEnum newsType = supportedNewsType();
		log.info("请求抓取[{}]数据", newsType.getDescription());
		new Thread(new Runnable() {
			@Override
			public void run() {
				String realUrl = retrieveUrl();
				if (!StringUtils.isBlank(url)) {
					realUrl = url;
				}
				try {
					List<NewsDTO> data = retrieveData(url);
					log.info("从{}抓取[{}]数据[{}]条", realUrl, newsType.getDescription(), data.size());
					newsApi.batchSave(data);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("从{}抓取[{}]数据并保存异常[{}]!", realUrl, newsType.getDescription(), e.getMessage());
				}
			}
		}).start();
	}

}
