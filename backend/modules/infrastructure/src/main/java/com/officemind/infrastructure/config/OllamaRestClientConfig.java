package com.officemind.infrastructure.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * spring-ai 1.0.0-M1's OllamaAutoConfiguration builds OllamaApi from
 * whatever RestClient.Builder bean is in context, but exposes no
 * application.yml property to configure its timeout (see
 * OllamaConnectionProperties — it only has baseUrl). Spring Boot's default
 * RestClient.Builder (backed by OkHttp here, since it's on the classpath)
 * uses OkHttp's 10s default read timeout, which llama3.2:1b can exceed on
 * a cold call or under Docker Desktop resource pressure — causing a
 * "Socket closed" / ResourceAccessException after almost exactly 10s.
 *
 * This bean overrides the default (via @ConditionalOnMissingBean on
 * Spring Boot's own RestClientAutoConfiguration) with a longer read
 * timeout, and is picked up transparently by OllamaAutoConfiguration.
 */
@Configuration
public class OllamaRestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(10))
                .withReadTimeout(Duration.ofSeconds(120));
        ClientHttpRequestFactory factory = ClientHttpRequestFactories.get(settings);
        return RestClient.builder().requestFactory(factory);
    }
}
