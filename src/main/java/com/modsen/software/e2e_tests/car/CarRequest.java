package com.modsen.software.e2e_tests.car;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CarRequest {
    private Long id;
    private String color;
    private String model;
    private String vehicleNumber;
    private Long driverId;
    private Boolean isDeleted;
}
