package com.waben.option.thirdparty.service.news;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
 * https://www.wsj.com/news/economy
 * </p>
 */
@Service
public class WsjEconomyGrabService extends NewsGrabService {

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public NewsTypeEnum supportedNewsType() {
		return NewsTypeEnum.ECONOMY;
	}

	@Override
	public String retrieveUrl() {
		return "https://www.wsj.com/search?query=Economy&page=1";
	}

	@Override
	public List<NewsDTO> retrieveData(String url) throws Exception {
		List<NewsDTO> result = new ArrayList<>();
		String realUrl = retrieveUrl();
		if (!StringUtils.isBlank(url)) {
			realUrl = url;
		}
		Document doc = JsoupUtil.getDocument(realUrl);
		if (doc != null) {
			Elements articles = doc.select("#root article");
			if (articles != null && articles.size() > 0) {
				System.out.println(articles.size());
				for (int i = 0; i < articles.size(); i++) {
					Element article = articles.get(i);
					// 编号
					String newsNo = article.attr("data-id");
					// 标题
					Elements titleEle = article.select("article h3 a");
					if (titleEle == null || titleEle.size() == 0) {
						continue;
					}
					String title = titleEle.get(0).text().trim();
					// 内容
					Elements contentEle = article.select("article > div > div > p span");
					if (contentEle == null || contentEle.size() == 0) {
						continue;
					}
					String content = contentEle.get(0).text().trim();
					// 详情链接
					String detailUrl = titleEle.attr("href");
					Document detailDoc = JsoupUtil.getDocument(detailUrl);
					if (detailDoc == null) {
						continue;
					}
					// 封面图
					Elements coverImgEle = detailDoc.select("#article_sector > article .is-lead-inset img");
					if (coverImgEle == null || coverImgEle.size() == 0) {
						continue;
					}
					String coverImg = coverImgEle.attr("src");
					// 发布时间
					Elements publishTimeEle = detailDoc.select("#article_sector > article .timestamp");
					if (publishTimeEle == null || publishTimeEle.size() == 0) {
						continue;
					}
					String publishTimeStr = publishTimeEle.get(0).text().trim().replaceAll("Updated ", "");
					boolean isAm = false;
					if (publishTimeStr.indexOf("am") > 0) {
						publishTimeStr = publishTimeStr.substring(0, publishTimeStr.indexOf("am")).trim();
						isAm = true;
					} else if (publishTimeStr.indexOf("pm") > 0) {
						publishTimeStr = publishTimeStr.substring(0, publishTimeStr.indexOf("pm")).trim();
					} else {
						continue;
					}
					String[] publishTimeArr = publishTimeStr.split(" ");
					int year = Integer.parseInt(publishTimeArr[2].trim());
					int month = getMonth(publishTimeArr[0].substring(0, publishTimeArr[0].indexOf(".")).trim());
					int day = Integer.parseInt(publishTimeArr[1].substring(0, publishTimeArr[1].indexOf(",")).trim());
					int hour = Integer.parseInt(publishTimeArr[3].split(":")[0].trim());
					if (!isAm && hour < 12) {
						hour = hour + 12;
					}
					int minute = Integer.parseInt(publishTimeArr[3].split(":")[1].trim());
					String publishTime = year + "-" + timeIntToStr(month) + "-" + timeIntToStr(day) + " "
							+ timeIntToStr(hour) + ":" + timeIntToStr(minute) + ":00";
					// 构建对象
					NewsDTO news = new NewsDTO();
					news.setNewsNo(newsNo);
					news.setType(supportedNewsType());
					news.setTitle(title);
					news.setContent(content);
					news.setCoverImg(coverImg);
					news.setPublishTime(LocalDateTime.parse(publishTime, formatter));
					result.add(news);
				}
			}
		}
		return result;
	}

	private String timeIntToStr(int timeInt) {
		return timeInt < 10 ? ("0" + timeInt) : ("" + timeInt);
	}

	private int getMonth(String monthStr) {
		if ("Jan".equals(monthStr.trim())) {
			return 1;
		} else if ("Feb".equals(monthStr.trim())) {
			return 2;
		} else if ("March".equals(monthStr.trim())) {
			return 3;
		} else if ("April".equals(monthStr.trim())) {
			return 4;
		} else if ("May".equals(monthStr.trim())) {
			return 5;
		} else if ("June".equals(monthStr.trim())) {
			return 6;
		} else if ("July".equals(monthStr.trim())) {
			return 7;
		} else if ("Aug".equals(monthStr.trim())) {
			return 8;
		} else if ("Sept".equals(monthStr.trim())) {
			return 9;
		} else if ("Oct".equals(monthStr.trim())) {
			return 10;
		} else if ("Nov".equals(monthStr.trim())) {
			return 11;
		} else if ("Dec".equals(monthStr.trim())) {
			return 12;
		}
		return 0;
	}

}
