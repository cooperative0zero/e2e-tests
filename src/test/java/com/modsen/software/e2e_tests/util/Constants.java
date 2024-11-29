package com.modsen.software.e2e_tests.util;

import com.modsen.software.e2e_tests.car.CarRequest;
import com.modsen.software.e2e_tests.driver.DriverRequest;
import com.modsen.software.e2e_tests.passenger.PassengerRequest;
import com.modsen.software.e2e_tests.rating.RatingRequest;
import com.modsen.software.e2e_tests.ride.RideRequest;

import java.time.OffsetDateTime;

public class Constants {
    public static DriverRequest driverRequest;
    public static DriverRequest anotherDriverRequest;
    public static CarRequest carRequest;
    public static CarRequest anotherCarRequest;
    public static PassengerRequest passengerRequest;
    public static PassengerRequest anotherPassengerRequest;
    public static RatingRequest ratingRequest;
    public static RatingRequest anotherRatingRequest;
    public static RideRequest rideRequest;
    public static RideRequest anotherRideRequest;

    static {
        driverRequest = DriverRequest.builder()
                .fullName("First Middle Last")
                .email("example@mail.com")
                .phone("987654321")
                .gender("MALE")
                .status("AVAILABLE")
                .isDeleted(false)
                .build();

        anotherDriverRequest = DriverRequest.builder()
                .fullName("First2 Middle2 Last2")
                .email("example2@mail.com")
                .phone("123456789")
                .gender("MALE")
                .status("AVAILABLE")
                .isDeleted(false)
                .build();

        carRequest = CarRequest.builder()
                .color("color1")
                .model("model1")
                .vehicleNumber("AH09823")
                .isDeleted(false)
                .build();

        anotherCarRequest = CarRequest.builder()
                .color("color2")
                .model("model2")
                .vehicleNumber("BN09823")
                .isDeleted(false)
                .build();

        passengerRequest = PassengerRequest.builder()
                .fullName("First Middle Last")
                .email("example@mail.com")
                .phone("987654321")
                .rating(1f)
                .isDeleted(false)
                .build();

        anotherPassengerRequest = PassengerRequest.builder()
                .fullName("First2 Middle2 Last2")
                .email("example2@mail.com")
                .phone("123456789")
                .rating(2f)
                .isDeleted(false)
                .build();

        ratingRequest = RatingRequest.builder()
                .driverId(1L)
                .passengerId(1L)
                .rating(((byte) 5))
                .comment("Comment 1")
                .creationDate(OffsetDateTime.now())
                .isByPassenger(true)
                .build();

        anotherRatingRequest = RatingRequest.builder()
                .driverId(2L)
                .passengerId(2L)
                .rating(((byte) 2))
                .comment("Comment 2")
                .creationDate(OffsetDateTime.now())
                .isByPassenger(false)
                .build();

        rideRequest = RideRequest.builder()
                .passengerId(1L)
                .departureAddress("Departure address 1")
                .destinationAddress("Destination address 1")
                .creationDate(OffsetDateTime.now())
                .rideStatus("CREATED")
                .build();

        anotherRideRequest = RideRequest.builder()
                .passengerId(2L)
                .departureAddress("Departure address 2")
                .destinationAddress("Destination address 2")
                .creationDate(OffsetDateTime.now())
                .rideStatus("CREATED")
                .build();
    }
}
