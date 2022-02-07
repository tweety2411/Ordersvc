package com.heypli.ordersvc.domain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
public class OrderInfo {
    private Integer      orderId;
    private String   userId;
    private Date     orderDate;

    @NotNull
    private List<Integer> prodIdList;
}
