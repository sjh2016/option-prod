package com.waben.option.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.component.SpringContext;
import com.waben.option.common.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class HttpUtil {

    public static <T> T requestGet(String url, ResponseExecutor<T> executor) {
        OkHttpClient okHttpClient = SpringContext.getBean("okHttpClient", OkHttpClient.class);
        try {
            Request request = new Request.Builder().url(url).get().build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return executor.execute(response);
            } else {
                log.error("HTTP_GET_FAIL|{}|{}|{}", url, response.code(), response.message());
                throw new ServerException(1015);
            }
        } catch (IOException e) {
            log.error("HTTP_GET_FAIL", e);
            throw new ServerException(1015);
        }
    }

    public static <T> T requestGet(String url, ResponseDataExecutor<T> executor) {
        OkHttpClient okHttpClient = SpringContext.getBean("okHttpClient", OkHttpClient.class);
        try {
            Request request = new Request.Builder().url(url).get().build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return executor.execute(response.body().string());
            } else {
                log.error("HTTP_GET_FAIL|{}|{}|{}", url, response.code(), response.message());
                throw new ServerException(1015);
            }
        } catch (IOException e) {
            log.error("HTTP_GET_FAIL", e);
            throw new ServerException(1015);
        }
    }

    public static <T> T requestPostJsonData(String url, Map<String, String> headerMap, Map<String, Object> paramMap, ResponseExecutor<T> executor) {
        OkHttpClient okHttpClient = SpringContext.getBean("okHttpClient", OkHttpClient.class);
        ObjectMapper objectMapper = SpringContext.getBean(ObjectMapper.class);
        try {
            Request.Builder requestBuilder = new Request.Builder();
            buildHeader(requestBuilder, headerMap);
            Request request = requestBuilder.url(url)
                    .post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),
                            objectMapper.writeValueAsString(paramMap))).build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return executor.execute(response);
            } else {
                log.error("HTTP_GET_FAIL|{}|{}|{}", url, response.code(), response.message());
                throw new ServerException(1015);
            }
        } catch (Exception e) {
            log.error("HTTP_GET_FAIL", e);
            if (e instanceof ServerException) {
                throw (ServerException) e;
            }
            throw new ServerException(1015);
        }
    }

    public static <T> T requestPostJsonData(String url, Map<String, String> headerMap, Map<String, Object> paramMap, ResponseDataExecutor<T> executor) {
        OkHttpClient okHttpClient = SpringContext.getBean("okHttpClient", OkHttpClient.class);
        ObjectMapper objectMapper = SpringContext.getBean(ObjectMapper.class);
        try {
            Request.Builder requestBuilder = new Request.Builder();
            buildHeader(requestBuilder, headerMap);
            Request request = requestBuilder.url(url)
                    .post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),
                            objectMapper.writeValueAsString(paramMap))).build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseString = response.body().string();
                log.info("http request: {}, {}", url, paramMap);
                log.info("http response: {}", responseString.length() > 300 ? responseString.substring(0, 300) + "..." : responseString);
                return executor.execute(responseString);
            } else {
                log.error("HTTP_GET_FAIL|{}|{}|{}", url, response.code(), response.message());
                throw new ServerException(1015);
            }
        } catch (Exception e) {
            log.error("HTTP_GET_FAIL|" + url + "|" + paramMap, e);
            if (e instanceof ServerException) {
                throw (ServerException) e;
            }
            throw new ServerException(1015);
        }
    }

    public static <T> T requestPostFormData(String url, Map<String, String> headMap, Map<String, Object> paramMap, ResponseExecutor<T> executor) {
        OkHttpClient okHttpClient = SpringContext.getBean("okHttpClient", OkHttpClient.class);
        try {
            Request request = buildRequest(url, headMap, paramMap);
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return executor.execute(response);
            } else {
                log.error("HTTP_GET_FAIL|{}", response.body().toString());
                throw new ServerException(1015);
            }
        } catch (IOException e) {
            log.error("HTTP_GET_FAIL", e);
            throw new ServerException(1015);
        }
    }

    public static <T> T requestPostFormData(String url, Map<String, String> headMap, Map<String, Object> paramMap, ResponseDataExecutor<T> executor) {
        OkHttpClient okHttpClient = SpringContext.getBean("okHttpClient", OkHttpClient.class);
        try {
            Request request = buildRequest(url, headMap, paramMap);
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return executor.execute(response.body().string());
            } else {
                log.error("HTTP_GET_FAIL|{}|{}|{}", url, response.code(), response.message());
                throw new ServerException(1015);
            }
        } catch (IOException e) {
            log.error("HTTP_GET_FAIL", e);
            throw new ServerException(1015);
        }
    }

    private static Request buildRequest(String url, Map<String, String> headerMap, Map<String, Object> paramMap) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        buildHeader(requestBuilder, headerMap);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if(paramMap != null && !paramMap.isEmpty()) {
            for(Map.Entry<String, Object> entry : paramMap.entrySet()) {
                formBodyBuilder.add(entry.getKey(), entry.getValue().toString());
            }
        }
        Request request = requestBuilder.post(formBodyBuilder.build()).build();
        return request;
    }

    private static void buildHeader(Request.Builder requestBuilder, Map<String, String> headerMap) {
        if(headerMap != null && !headerMap.isEmpty()) {
            Headers.Builder headersBuilder = new Headers.Builder();
            for(Map.Entry<String, String> entry : headerMap.entrySet()) {
                headersBuilder.set(entry.getKey(), entry.getValue());
            }
            requestBuilder.headers(headersBuilder.build());
        }
    }

    public interface ResponseExecutor<T> {

        T execute(Response response);

    }

    public interface ResponseDataExecutor<T> {

        T execute(String data);

    }

}
