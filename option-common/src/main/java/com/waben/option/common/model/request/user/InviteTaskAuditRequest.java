package com.waben.option.common.model.request.user;

import com.waben.option.common.model.enums.InviteAuditStatusEnum;
import lombok.Data;

@Data
public class InviteTaskAuditRequest {

    private Long id;

    private InviteAuditStatusEnum status;

}
