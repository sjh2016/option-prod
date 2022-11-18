package com.waben.option.common.model.query;

import com.waben.option.common.model.enums.CurrencyEnum;
import lombok.Data;
import java.util.List;

@Data
public class UserAccountQuery {

    List<Long> uidList;

    CurrencyEnum currency;
}
