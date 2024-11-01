package com.modsen.software.e2e_tests.component.ride;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:feature/ride",
        glue = "com.modsen.software.e2e_tests.component.ride.step"
)
public class RideServiceTest {
}
