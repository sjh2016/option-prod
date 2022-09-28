package com.waben.option.common.model.dto.resource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
public class WithdrawConfigDTO {

	@ApiModelProperty("星期一到星期日")
    private List<Integer> weekList;

	@ApiModelProperty("每日提现开始时间")
    private LocalTime startTime;

	@ApiModelProperty("每日提现结束时间")
    private LocalTime endTime;

    @ApiModelProperty("最小提现金额")
    private BigDecimal minAmount;

}
