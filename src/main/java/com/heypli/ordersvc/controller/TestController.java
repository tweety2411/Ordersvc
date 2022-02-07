package com.heypli.ordersvc.controller;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final RestTemplate restTemplate;

    @RequestMapping("/webclient")
    public void test() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(10000))
                .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("http://localhost:8080/prod/test").build();

        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.GET);

        Mono<String> result = client.get().retrieve().bodyToMono(String.class);
        String r = result.block();
        System.out.println("###  r " + r);
    }

    @RequestMapping("/restTemplate")
    public ResponseEntity<String> restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(5000); // 읽기시간초과, ms
        factory.setConnectTimeout(3000); // 연결시간초과, ms
        CloseableHttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(100)// connection pool 적용
            .setMaxConnPerRoute(5) // connection pool 적용
            .build();
        factory.setHttpClient(httpClient); // 동기실행에 사용될 HttpClient 세팅
        RestTemplate restTemplate = new RestTemplate(factory);

        ResponseEntity<String> result = restTemplate.exchange("http://localhost:8080/prod/test", HttpMethod.GET, null, String.class);
        return result;
    }
}
