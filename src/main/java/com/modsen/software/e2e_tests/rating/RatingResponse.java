package com.modsen.software.e2e_tests.rating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RatingResponse {

    private Long id;
    private Long driverId;
    private Long passengerId;
    private Byte rating;
    private String comment;
    private OffsetDateTime creationDate;
    private Boolean isByPassenger;
}