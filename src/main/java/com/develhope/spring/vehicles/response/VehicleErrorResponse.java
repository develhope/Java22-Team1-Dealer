package com.develhope.spring.vehicles.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleErrorResponse {
    private int code;
    private String message;

}
