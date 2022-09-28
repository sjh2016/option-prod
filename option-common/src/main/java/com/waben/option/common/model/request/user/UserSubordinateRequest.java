package com.waben.option.common.model.request.user;

import lombok.Data;

@Data
public class UserSubordinateRequest {
	
	private Long userId;
	
	private int page;
	
	private int size;
	
}
