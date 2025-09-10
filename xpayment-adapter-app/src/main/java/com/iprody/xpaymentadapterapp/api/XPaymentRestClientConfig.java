package com.iprody.xpaymentadapterapp.api;

import com.iprody.xpaymentadapterapp.api.client.DefaultApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class XPaymentRestClientConfig {
    @Bean
    RestTemplate xpaymentRestTemplate(
        @Value("${app.x-payment-api.client.username}") String username,
        @Value("${app.x-payment-api.client.password}") String password,
        @Value("${app.x-payment-api.client.account}") String xPayAccount
    ) {
        final RestTemplate rt = new RestTemplate();
        rt.getInterceptors().add((req, body, ex) -> {
            req.getHeaders().setBasicAuth(username, password);
            req.getHeaders().add("X-Pay-Account", xPayAccount);
            return ex.execute(req, body);
        });
        return rt;
    }
    @Bean
    ApiClient xpaymentApiClient(
        @Value("app.xpayment.client.url") String xPaymentUrl,
        RestTemplate xpaymentRestTemplate
    ) {
        final ApiClient apiClient = new ApiClient(xpaymentRestTemplate);
        apiClient.setBasePath(xPaymentUrl);
        return apiClient;
    }
    @Bean
    DefaultApi defaultApi(ApiClient apiClient) {
        return new DefaultApi(apiClient);
    }
}
