package com.waben.option.common.constants;

import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;

@Slf4j
public final class TopicConstants {

    public static final String TOPIC_ODDS = "{0}:odds:{1}:{2}"; //odds:{gameSymbol}:{instrumentSymbol或者gameIdEx}

    public static final String TOPIC_ISSUE = "issue:{0}:{1}"; //issue:{gameSymbol}:{instrumentSymbol或者gameIdEx}

    public static String getTopicOdds(String type, String gameSymbol, Object symbol) {
        return MessageFormat.format(TOPIC_ODDS, type, gameSymbol, symbol);
    }

    public static String getTopicIssue(String gameSymbol, Object symbol) {
        return MessageFormat.format(TOPIC_ISSUE, gameSymbol, symbol);
    }

}
