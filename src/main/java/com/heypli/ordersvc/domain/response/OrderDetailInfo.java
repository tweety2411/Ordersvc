package com.heypli.ordersvc.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class OrderDetailInfo {

    private int orderDetailId;
    private int orderId;
    private int prodId;
    private Date rgstDate;

    public OrderDetailInfo(int orderId, int prodId) {
        this.orderId = orderId;
        this.prodId = prodId;
    }
}
