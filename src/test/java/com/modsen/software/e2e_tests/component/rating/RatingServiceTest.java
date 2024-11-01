package com.modsen.software.e2e_tests.component.rating;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:feature/rating",
        glue = "com.modsen.software.e2e_tests.component.rating.step"
)
public class RatingServiceTest {
}
