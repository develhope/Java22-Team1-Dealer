package com.develhope.spring.Rent.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentResponse {
    private int code;
    private String message;
}
