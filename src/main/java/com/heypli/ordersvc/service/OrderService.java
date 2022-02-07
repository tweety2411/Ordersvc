package com.heypli.ordersvc.service;

import com.heypli.ordersvc.common.response.CommonResponse;
import com.heypli.ordersvc.domain.request.OrderInfo;

public interface OrderService {

    CommonResponse createOrder(OrderInfo orderInfo) throws Exception;
}
