package com.waben.option.common.model.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.component.SpringContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRemark<T> {

    private RemarkEnum type;

    private T data;

    public static enum RemarkEnum {

        TRADE,

    }

    public String toString() {
        try {
            return SpringContext.getBean(ObjectMapper.class).writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
