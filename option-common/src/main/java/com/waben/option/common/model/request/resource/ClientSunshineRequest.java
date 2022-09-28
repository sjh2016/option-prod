package com.waben.option.common.model.request.resource;

import com.waben.option.common.model.enums.SunshineTypeEnum;

import lombok.Data;

@Data
public class ClientSunshineRequest {

	private SunshineTypeEnum type;
	
    private String url;

}
