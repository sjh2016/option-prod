package com.waben.option.common.interfaces;

import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.Response;

public interface BaseAPI {

	default public <T> T getResponseData(Response<T> response) {
		if (response.getCode() != 0) {
			throw new ServerException(response.getCode(), response.getMsg());
		}
		return response.getData();
	}

}
