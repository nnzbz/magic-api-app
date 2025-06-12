package magicapi.config;


import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.ssssssss.magicapi.modules.http.HttpModule;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * http模块是基于RestTemplate封装而来，自定义RestTemplate，支持https
 */
@Configuration(proxyBeanMethods = false)
public class RestTemplateConfig {

    @Bean
    public SSLContext sslContext() {
        try {
            TrustStrategy acceptingTrustStrategy = (chain, authType) -> true;
            return SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public CloseableHttpClient closeableHttpClient(SSLContext sslContext) {
        SSLConnectionSocketFactory ssl           = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        HttpClientBuilder          clientBuilder = HttpClients.custom();
        return clientBuilder.setConnectionManager(PoolingHttpClientConnectionManagerBuilder
                .create().setSSLSocketFactory(ssl).build()).build();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(CloseableHttpClient closeableHttpClient) {
        return new HttpComponentsClientHttpRequestFactory(closeableHttpClient);
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);
        // 支持中文编码
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    /**
     * magic-api的http模块封装，加入自定义RestTemplate
     */
    @Bean
    public HttpModule magicHttpModule(RestTemplate restTemplate) {
        return new HttpModule(restTemplate);
    }
}
