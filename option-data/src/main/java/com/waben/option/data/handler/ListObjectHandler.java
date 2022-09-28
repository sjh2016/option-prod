package com.waben.option.data.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.component.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class ListObjectHandler<T>  extends BaseTypeHandler<List<T>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            if(parameter != null && parameter.size() > 0) {
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
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return readValue(rs.getString(columnName));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return readValue(rs.getString(columnIndex));
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return readValue(cs.getString(columnIndex));
    }

    private List<T> readValue(String value) {
        try {
            if(!StringUtils.isBlank(value)) {
                List<T> list = getObjectMapper().readValue(value, new TypeReference<List<T>>() {
                });
                return list;
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

}
