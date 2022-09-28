package com.waben.option.common.configuration;

import com.waben.option.common.component.LocaleContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Peter
 * @date: 2021/6/4 18:36
 */
@Configuration
public class LanguageInitConfig {

    @Value("${language.locale:en_US,es_ES,fr_FR,ja_JP,ko_KR,pt_BR,zh_CN,id_ID}")
    private String locale;

    @Bean
    public void init() {
        String[] localeList = locale.split(",");
        for (String s : localeList) {
            LocaleContext.put(s.split("_")[0], s.split("_")[1]);
        }
    }
}
