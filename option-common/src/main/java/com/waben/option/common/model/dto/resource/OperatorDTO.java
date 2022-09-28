package com.waben.option.common.model.dto.resource;

import lombok.Data;

@Data
public class OperatorDTO {

    /**
     * 运营商id
     */
    private Integer id;

    /**
     * 运营商
     */
    private String operator;

    /**
     * 国家id
     */
    private Integer countryId;

}
