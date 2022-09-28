package com.waben.option.common.model.request.account;

import com.waben.option.common.model.bean.AccountTransactionBean;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
public class AccountTransactionRequest {

    private Long userId;
    private List<AccountTransactionBean> transactionBeanList;
}
