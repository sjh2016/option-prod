package com.waben.option.common.model.enums;

public enum DataSourceType {

    DEFAULT("ds0", "ds0"),
    DATASOURCE_1("ds1", "ds1"),

    ;

    private String name;

    private String identity;

    DataSourceType(String name, String identity) {
        this.name = name;
        this.identity = identity;
    }

    public String getName() {
        return name;
    }

    public String getIdentity() {
        return identity;
    }

    public static DataSourceType getDataSourceTypeByName(String name) {
        for (DataSourceType dataSourceType : DataSourceType.values()) {
            if (dataSourceType.name.equals(name)) {
                return dataSourceType;
            }
        }
        throw new RuntimeException("db is not exist." + name);
    }
}
