package com.modsen.software.e2e_tests.component.driver;

import com.modsen.software.e2e_tests.util.E2EConfig;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:feature/driver",
        glue = "com.modsen.software.e2e_tests.component.driver.step"
)
public class DriverServiceTest {
}
