package magicapi.config;


import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

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
//        HttpComponentsClientHttpRequestFactory factory =
//                new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
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
     * magic-api的 http模块封装，加入自定义RestTemplate
     * @param restTemplate
     * @return
     */
    @Bean
    public HttpModule magicHttpModule(RestTemplate restTemplate) {
        return new HttpModule(restTemplate);
    }
}
