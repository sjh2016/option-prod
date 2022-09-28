package com.waben.option.thirdparty.service.news;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.waben.option.common.model.dto.resource.NewsDTO;
import com.waben.option.common.model.enums.NewsTypeEnum;
import com.waben.option.common.util.JsoupUtil;
import com.waben.option.thirdparty.service.NewsGrabService;

/**
 * 经济要闻
 * 
 * <p>
 * https://www.ft.com/global-economy
 * </p>
 */
@Service
public class FtEconomyGrabService extends NewsGrabService {

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public NewsTypeEnum supportedNewsType() {
		return NewsTypeEnum.ECONOMY;
	}

	public String retrieveUrl() {
		return "https://www.ft.com/global-economy";

	}

	public List<NewsDTO> retrieveData(String url) throws Exception {
		List<NewsDTO> result = new ArrayList<>();
		String realUrl = retrieveUrl();
		if (!StringUtils.isBlank(url)) {
			realUrl = url;
		}
		Document doc = JsoupUtil.getDocument(realUrl);
		if (doc != null) {
			Elements articles = doc.select(".js-stream-list li");
			if (articles != null && articles.size() > 0) {
				for (int i = 0; i < articles.size(); i++) {
					Element article = articles.get(i).select(".stream-item").get(0);
					// 编号
					String newsNo = article.attr("data-id");
					// 标题
					Elements titleEle = article.select(".o-teaser__heading a");
					if (titleEle == null || titleEle.size() == 0) {
						continue;
					}
					String title = titleEle.get(0).text().trim();
					// 内容
					Elements contentEle = article.select(".o-teaser__standfirst a");
					if (contentEle == null || contentEle.size() == 0) {
						continue;
					}
					String content = contentEle.get(0).text().trim();
					// 封面图
					Elements coverImgEle = article.select(".o-teaser__image");
					if (coverImgEle == null || coverImgEle.size() == 0) {
						continue;
					}
					String coverImg = coverImgEle.attr("data-src");
					// 发布时间
					Elements publishTimeEle = article.select("time");
					String publishTimeStr = "";
					if (publishTimeEle == null || publishTimeEle.size() == 0) {
						publishTimeStr = "2022-02-10T00:00:00+0000";
						continue;
					} else {
						publishTimeStr = publishTimeEle.get(0).attr("datetime");
					}
					publishTimeStr = publishTimeStr.substring(0, publishTimeStr.indexOf("+"));
					publishTimeStr = publishTimeStr.replaceAll("T", " ");
					// 构建对象
					NewsDTO news = new NewsDTO();
					news.setNewsNo(newsNo);
					news.setType(supportedNewsType());
					news.setTitle(title);
					news.setContent(content);
					news.setCoverImg(coverImg);
					LocalDateTime dateTime = LocalDateTime.parse(publishTimeStr, formatter);
					dateTime = dateTime.plusSeconds(TimeZone.getDefault().getRawOffset() / 1000);
					news.setPublishTime(dateTime);
					result.add(news);
				}
			}
		}
		return result;
	}

}
