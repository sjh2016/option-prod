package com.waben.option.data.handler.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.component.SpringContext;
import com.waben.option.common.model.dto.resource.BannerDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.List;

@Slf4j
public class ListBannerEntityTypeHandler extends BaseTypeHandler<List<BannerDTO.BannerLanguageDTO>> {

    private ObjectMapper getObjectMapper() {
        return SpringContext.getBean(ObjectMapper.class);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<BannerDTO.BannerLanguageDTO> parameter, JdbcType jdbcType) throws SQLException {
        try {
            if (parameter != null && parameter.size() > 0) {
                ps.setString(i, getObjectMapper().writeValueAsString(parameter));
            } else {
                ps.setNull(i, Types.VARCHAR);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public List<BannerDTO.BannerLanguageDTO> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return readValue(rs.getString(columnName));
    }

    @Override
    public List<BannerDTO.BannerLanguageDTO> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return readValue(rs.getString(columnIndex));
    }

    @Override
    public List<BannerDTO.BannerLanguageDTO> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return readValue(cs.getString(columnIndex));
    }

    private List<BannerDTO.BannerLanguageDTO> readValue(String value) {
        try {
            if (!StringUtils.isBlank(value)) {
                List<BannerDTO.BannerLanguageDTO> list = getObjectMapper().readValue(value, new TypeReference<List<BannerDTO.BannerLanguageDTO>>() {
                });
                return list;
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

}
