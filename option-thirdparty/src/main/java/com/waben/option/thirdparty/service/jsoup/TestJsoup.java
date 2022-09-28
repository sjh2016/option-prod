package com.waben.option.thirdparty.service.jsoup;

import com.waben.option.common.util.JsoupUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestJsoup {

    public static void main(String[] args) {
        Document doc = JsoupUtil.getDocument("https://en.solarbe.com/home/article/index/catId/26");
        if (doc != null) {
            String s = doc.body().toString();
            Elements eles = doc.select(".js-left > div");
            if (eles != null && eles.size() > 1) {
                Elements articles = eles.get(1).select(".js-article");
                if (articles != null && articles.size() > 0) {
                    for (int i = 0; i < articles.size(); i++) {
                        Element article = articles.get(i);
                        // 过滤置顶数据
                        Elements tagTop = article.select(".js-tag-top");
                        if (tagTop != null && tagTop.size() > 0) {
                            String styleTop = tagTop.get(0).attr("style");
                            if ("".equals(styleTop)) {
                                continue;
                            }
                        }
                    }
                }
            }
        }
    }
}
