package com.waben.option.common.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import lombok.extern.slf4j.Slf4j;

/**
 * jsoup 爬虫 工具类
 */
@Slf4j
public class JsoupUtil {

	public static final int TIMEOUT = 10000;

	public static Document getDocument(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url)
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2")
					.timeout(TIMEOUT).get();
		} catch (IOException e) {
			log.info("========Jsoup url=" + url + "， 连接失败，" + e.getMessage());
		}
		return doc;
	}

}