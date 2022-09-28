package com.waben.option.common.configuration;

// @Configuration
public class SQLParserConfig {

//    @PostConstruct
//    public void load() {
//        // 提前解析一条公共的 sql
//        String databaseType = "MySQL";
//        SQLParserEngine parseEngine = SQLParserEngineFactory.getSQLParserEngine(databaseType);
//        String sql = "select 'X'";
//        parseEngine.parse(sql, true);
//
//        // 提前实例化相关对象
//        SQLParser sqlParser = SQLParserFactory.newInstance(databaseType, sql);
//        ParseASTNode execute = (ParseASTNode) sqlParser.parse();
//        ParseTree parseTree = execute.getRootNode();
//        ParseTreeVisitorFactory.newInstance(databaseType, VisitorRule.valueOf(parseTree.getClass()));
//    }
}
