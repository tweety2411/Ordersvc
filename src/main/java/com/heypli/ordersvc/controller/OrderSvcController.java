package com.heypli.ordersvc.controller;

import com.heypli.ordersvc.common.response.CommonResponse;
import com.heypli.ordersvc.domain.request.OrderInfo;
import com.heypli.ordersvc.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RequestMapping("/order")
@RestController
@RequiredArgsConstructor
public class OrderSvcController {

    private final OrderService orderService;

    @PostMapping("/createOrder")
    public ResponseEntity<CommonResponse> createOrder(@Valid @RequestBody OrderInfo orderInfo) throws Exception {
        CommonResponse response = orderService.createOrder(orderInfo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
