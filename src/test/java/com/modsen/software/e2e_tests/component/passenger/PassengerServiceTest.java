package com.modsen.software.e2e_tests.component.passenger;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:feature/passenger",
        glue = "com.modsen.software.e2e_tests.component.passenger.step"
)
public class PassengerServiceTest {
}
