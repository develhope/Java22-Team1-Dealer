package com.develhope.spring.order.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderResponse {
    private int code;

    private String message;
}
