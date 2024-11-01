package com.modsen.software.e2e_tests.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DriverResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private Long carId;
    private Float rating;
    private BigDecimal balance;
    private String status;
    private Boolean isDeleted;
}
