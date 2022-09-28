package com.waben.option.common.model.request.user;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GenerateSubordinateRequest {

	private Long parentId;
	
	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private Integer num;

}
