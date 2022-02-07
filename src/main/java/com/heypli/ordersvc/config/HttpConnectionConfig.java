package com.heypli.ordersvc.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class HttpConnectionConfig {

    private static final int MAX_TOTAL_CONNECTION = 200;
    private static final int MAX_DEFAULT_ROUTE_CONNECTION = 20;
    private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000; // 20초
    private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME = 60 * 1000; // 60초

    // Determines the timeout in milliseconds until a connection is established.
    private static final int CONNECT_TIMEOUT = 30000;
    // The timeout when requesting a connection from the connection manager.
    private static final int REQUEST_TIMEOUT = 30000;
    // The timeout for waiting for data
    private static final int SOCKET_TIMEOUT = 60000;


    @Bean(name = "poolManager")
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        // 허용되는 최대 커넥션 수
        poolingHttpClientConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTION);
        // setMaxPerRoute는 경로를 미리 알고 있는 경우 사용
        // setMaxPerRoute에 의해 경로가 지정되지 않은 호출에 대해 connection 갯수를 설정
        // 라우팅할 경로에 대한 커넥션
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(MAX_DEFAULT_ROUTE_CONNECTION);

        return poolingHttpClientConnectionManager;
    }

    @Bean(name = "keepAliveStrategy")
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
       return new ConnectionKeepAliveStrategy() {
           @Override
           public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
               HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
               while(it.hasNext()) {
                   HeaderElement he = it.nextElement();
                   String param = he.getName();
                   String value = he.getValue();
                   if(value != null && param.equalsIgnoreCase("timeout")) {
                       return Long.parseLong(value) * 1000;
                   }
               }
               return DEFAULT_KEEP_ALIVE_TIME_MILLIS;
           }
       };
    }


    @Bean(name ="idleConnectionMonitor")
    public Runnable idleConnectionMonitor(final PoolingHttpClientConnectionManager connectionManager) {
        return new Runnable() {
            @Override
            @Scheduled(fixedDelay = 10000)
            public void run() {
                try {
                    if (connectionManager != null) {
                        connectionManager.closeExpiredConnections();
                        connectionManager.closeIdleConnections(CLOSE_IDLE_CONNECTION_WAIT_TIME, TimeUnit.MILLISECONDS);
                    } else {
                        log.trace("run IdleConnectionMonitor - Http Client Connection manager is not initialised");
                    }
                } catch (Exception e) {
                    log.error("run IdleConnectionMonitor - Exception occurred. msg={}, e={}", e.getMessage(), e);
                }
            }
        };
    }

    @Bean(name = "httpClient")
    public CloseableHttpClient httpClient(final PoolingHttpClientConnectionManager poolManager,
                                          final ConnectionKeepAliveStrategy strategy) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT).build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolManager)
                .setKeepAliveStrategy(strategy)
                .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory(final CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient);
        return clientHttpRequestFactory;
    }

    @Bean(name = "restTemplate")
    public RestTemplate restTemplate(final HttpComponentsClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }
}
