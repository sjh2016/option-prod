package com.waben.option.common.model.dto.vest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * 响应实体
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppVestResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码，200为正常，其他状态码为业务异常
     */
    private String code = "200";
    /**
     * 响应实体对象
     */
    private T result;
    /**
     * 消息提示
     */
    private String message = "响应成功";

    public AppVestResponse() {
    }

    public AppVestResponse(T result) {
        this.result = result;
    }

    public AppVestResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public AppVestResponse(String code, T result, String message) {
        this.code = code;
        this.result = result;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
