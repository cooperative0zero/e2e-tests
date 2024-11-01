package com.modsen.software.e2e_tests.passenger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PassengerRequest {

    private Long id;

    private String fullName;

    private String email;

    private String phone;

    private Boolean isDeleted;

    private Float rating;
}

