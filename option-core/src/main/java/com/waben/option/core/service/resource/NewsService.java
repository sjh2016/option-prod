package com.waben.option.core.service.resource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.model.dto.resource.NewsDTO;
import com.waben.option.data.entity.resource.News;
import com.waben.option.data.repository.resource.NewsDao;

@Service
public class NewsService {

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Resource
	private NewsDao newsDao;

	@Resource
	private IdWorker idWorker;

	@Resource
	private ModelMapper modelMapper;

	public List<NewsDTO> list(String publishTime, int size) {
		Page<News> page = new Page<>(1, size);
		QueryWrapper<News> query = new QueryWrapper<>();
		if (!StringUtils.isBlank(publishTime)) {
			LocalDateTime time = LocalDateTime.parse(publishTime, formatter);
			query.lt(News.PUBLISH_TIME, time);
		}
		query.orderByDesc(News.PUBLISH_TIME);
		IPage<News> pageData = newsDao.selectPage(page, query);
		return pageData.getRecords().stream().map(temp -> modelMapper.map(temp, NewsDTO.class))
				.collect(Collectors.toList());
	}

	@Transactional
	public void batchSave(List<NewsDTO> newsList) {
		if (newsList != null && newsList.size() > 0) {
			for (NewsDTO newsDTO : newsList) {
				QueryWrapper<News> query = new QueryWrapper<>();
				query.eq(News.NEWS_NO, newsDTO.getNewsNo());
				Integer check = newsDao.selectCount(query);
				if (check != null && check.intValue() > 0) {
					continue;
				}
				News news = modelMapper.map(newsDTO, News.class);
				news.setId(idWorker.nextId());
				newsDao.insert(news);
			}
		}
	}

}
