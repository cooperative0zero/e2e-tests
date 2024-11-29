package com.modsen.software.e2e_tests.ride;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RideResponse {

    private Long id;
    private Long driverId;
    private Long passengerId;
    private String departureAddress;
    private String destinationAddress;
    private String rideStatus;
    private OffsetDateTime creationDate;
    private OffsetDateTime completionDate;
    private BigDecimal price;
}