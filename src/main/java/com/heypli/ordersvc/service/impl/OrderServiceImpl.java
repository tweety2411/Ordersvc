package com.heypli.ordersvc.service.impl;

import com.google.gson.Gson;
import com.heypli.ordersvc.common.response.CommonResponse;
import com.heypli.ordersvc.domain.request.OrderInfo;
import com.heypli.ordersvc.domain.request.Product;
import com.heypli.ordersvc.domain.response.OrderDetailInfo;
import com.heypli.ordersvc.domain.response.ProductInfoResponse;
import com.heypli.ordersvc.mapper.OrderServiceMapper;
import com.heypli.ordersvc.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderServiceMapper orderServiceMapper;
    private final RestTemplate  restTemplate;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CommonResponse createOrder(OrderInfo orderInfo) throws Exception {
        // TODO
        //여기서 상품이 있는지 체크하는게 좋지만
        orderServiceMapper.insertOrder(orderInfo);

        List<OrderDetailInfo> list = new ArrayList<>();
        OrderDetailInfo info = null;
        for(Integer prodId : orderInfo.getProdIdList()) {
            if(getExecute(prodId)) {
                info = new OrderDetailInfo(orderInfo.getOrderId(), prodId);
                list.add(info);
            }
        }

        if(list.size() > 0 ) orderServiceMapper.insertOrderDetail(list);

        return CommonResponse.success();
    }

    private boolean getExecute(int prodId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        Product prod = new Product(prodId);
        String jsonStr = new Gson().toJson(prod);
        String url = "http://localhost:8080/prod/getProdInfo";

        ResponseEntity<ProductInfoResponse> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(jsonStr, headers), ProductInfoResponse.class);
        log.debug("response:" + response.getBody());
        return null != response.getBody();
    }
}
