package com.heypli.ordersvc.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class ProductInfoResponse {

    private long        prodId;
    private String      prodName;
    private String      prodDesc;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMd")
    private Date        rgstDate;
}
