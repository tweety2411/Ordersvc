package com.heypli.ordersvc.mapper;

import com.heypli.ordersvc.domain.request.OrderInfo;
import com.heypli.ordersvc.domain.response.OrderDetailInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderServiceMapper {

    void insertOrder(OrderInfo orderInfo) throws Exception;

    void insertOrderDetail(List<OrderDetailInfo> list) throws Exception;
}
