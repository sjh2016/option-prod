package com.waben.option.common.configuration;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Configuration
public class OKHttpClientConfig {

    @Value("${okhttp.readTimeout:30}")
    private int readTimeout;

    @Value("${okhttp.connectTimeout:30}")
    private int connectTimeout;

    @Bean("okHttpClient")
    public OkHttpClient okHttpClient(@Qualifier("sslSocketFactory") SSLSocketFactory sslSocketFactory,
                                     @Qualifier("trustManager") X509TrustManager trustManager,
                                     @Qualifier("hostnameVerifier") HostnameVerifier hostnameVerifier) {
        return new OkHttpClient.Builder().readTimeout(readTimeout, TimeUnit.SECONDS).connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .sslSocketFactory(sslSocketFactory, trustManager).hostnameVerifier(hostnameVerifier).build();
    }

    @Bean("noRedirectOkHttpClient")
    public OkHttpClient noRedirectOkHttpClient(@Qualifier("sslSocketFactory") SSLSocketFactory sslSocketFactory,
                                     @Qualifier("trustManager") X509TrustManager trustManager,
                                     @Qualifier("hostnameVerifier") HostnameVerifier hostnameVerifier) {
        return new OkHttpClient.Builder().followRedirects(false).followSslRedirects(false).readTimeout(readTimeout, TimeUnit.SECONDS)
                .connectTimeout(connectTimeout, TimeUnit.SECONDS).sslSocketFactory(sslSocketFactory, trustManager)
                .hostnameVerifier(hostnameVerifier).build();
    }

    @Bean("hostnameVerifier")
    public HostnameVerifier getHostnameVerifier() {
        return (hostname, session) -> true;
    }

    @Bean("trustManager")
    public X509TrustManager getTurstManager() {
        return new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

        };
    }

    @Bean("sslSocketFactory")
    public SSLSocketFactory getSSLSocketFactory(@Qualifier("trustManager") X509TrustManager trustManager) {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] { trustManager }, new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return sSLSocketFactory;
    }

}