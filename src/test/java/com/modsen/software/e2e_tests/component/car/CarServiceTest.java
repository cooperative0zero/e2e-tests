package com.modsen.software.e2e_tests.component.car;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:feature/car",
        glue = "com.modsen.software.e2e_tests.component.car.step"
)
public class CarServiceTest {
}
