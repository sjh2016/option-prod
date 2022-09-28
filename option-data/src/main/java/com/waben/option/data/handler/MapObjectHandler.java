package com.waben.option.data.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.component.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.Map;

@Slf4j
public class MapObjectHandler<T>  extends BaseTypeHandler<Map<String, T>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, T> parameter, JdbcType jdbcType) throws SQLException {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            if(!CollectionUtils.isEmpty(parameter)) {
                ps.setString(i, objectMapper.writeValueAsString(parameter));
            } else {
                ps.setNull(i, Types.VARCHAR);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private ObjectMapper getObjectMapper() {
        return SpringContext.getBean(ObjectMapper.class);
    }

    @Override
    public Map<String, T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return readValue(rs.getString(columnName));
    }

    @Override
    public Map<String, T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return readValue(rs.getString(columnIndex));
    }

    @Override
    public Map<String, T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return readValue(cs.getString(columnIndex));
    }

    private Map<String, T> readValue(String value) {
        try {
            if(!StringUtils.isBlank(value)) {
                Map<String, T> map = getObjectMapper().readValue(value, new TypeReference<Map<String, T>>() {
                });
                return map;
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

}
