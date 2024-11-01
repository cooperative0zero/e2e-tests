package com.modsen.software.e2e_tests.util;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import lombok.SneakyThrows;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ContextConfiguration(initializers = E2EConfig.Initializer.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class E2EConfig {
    private static final Network SHARED_NETWORK = Network.newNetwork();
    private static KafkaContainer KAFKA;

    private static GenericContainer<?> API_GATEWAY_SERVICE;
    private static GenericContainer<?> EUREKA_SERVICE;

    private static GenericContainer<?> PASSENGERS_SERVICE;
    private static GenericContainer<?> DRIVER_SERVICE;
    private static GenericContainer<?> RIDES_SERVICE;
    private static GenericContainer<?> RATINGS_SERVICE;

    static class Initializer implements
            ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        @SneakyThrows
        public void initialize(ConfigurableApplicationContext context) {
            final var environment = context.getEnvironment();
            final var apiExposedPort = requireNonNull(
                    environment.getProperty("gateway.exposed-port", Integer.class),
                    "API Exposed Port is null"
            );

            KAFKA = createKafkaContainer(environment);
            EUREKA_SERVICE = createEurekaServiceContainer(environment);
            API_GATEWAY_SERVICE = createGatewayServiceContainer(environment, apiExposedPort);

            Startables.deepStart(KAFKA, EUREKA_SERVICE, API_GATEWAY_SERVICE);

            PASSENGERS_SERVICE = createPassengerServiceContainer(environment);
            DRIVER_SERVICE = createDriverServiceContainer(environment);
            RIDES_SERVICE = createRidesServiceContainer(environment);
            RATINGS_SERVICE = createRatingsServiceContainer(environment);

            Startables.deepStart(PASSENGERS_SERVICE, DRIVER_SERVICE, RIDES_SERVICE, RATINGS_SERVICE).join();
//            Startables.deepStart(PASSENGERS_SERVICE).join();
            setPropertiesForConnections(environment);

            Thread.sleep(150000);
        }

        private KafkaContainer createKafkaContainer(Environment environment) {
            return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.4"))
                    .withNetwork(SHARED_NETWORK)
                    .withNetworkAliases("kafka");
        }

        private GenericContainer<?> createPassengerServiceContainer(Environment environment) {
            final var apiServiceImage = requireNonNull(
                    environment.getProperty(
                            "image.passenger-service",
                            String.class
                    ),
                    "Passenger service image is null"
            );

            final PostgreSQLContainer<?> PASSENGERS_DB = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                    .withNetwork(SHARED_NETWORK)
                    .withNetworkAliases("passengerdb-service")
                    .withDatabaseName("passenger");

            Startables.deepStart(PASSENGERS_DB).join();

            return new GenericContainer<>(apiServiceImage)
                    .withEnv("POSTGRES_DB", "passenger")
                    .withEnv("spring.datasource.username", PASSENGERS_DB.getUsername())
                    .withEnv("spring.datasource.password", PASSENGERS_DB.getPassword())
                    .withEnv("spring.kafka.bootstrap-servers", "kafka:9092")
                    .withNetwork(SHARED_NETWORK)
                    .withNetworkAliases("passenger-service");
        }

        private GenericContainer<?> createDriverServiceContainer(Environment environment) {
            final var apiServiceImage = requireNonNull(
                    environment.getProperty(
                            "image.driver-service",
                            String.class
                    ),
                    "Driver service image is null"
            );

            final PostgreSQLContainer<?> DRIVERS_DB = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                    .withNetwork(SHARED_NETWORK)
                    .withNetworkAliases("driverdb-service")
                    .withDatabaseName("drivers");

            Startables.deepStart(DRIVERS_DB).join();

            return new GenericContainer<>(apiServiceImage)
                    .withEnv("POSTGRES_DB", "drivers")
                    .withEnv("spring.datasource.username", DRIVERS_DB.getUsername())
                    .withEnv("spring.datasource.password", DRIVERS_DB.getPassword())
                    .withEnv("spring.kafka.bootstrap-servers", "kafka:9092")
                    .withNetwork(SHARED_NETWORK)
                    .withNetworkAliases("driver-service");
        }

        private GenericContainer<?> createRidesServiceContainer(Environment environment) {
            final var apiServiceImage = requireNonNull(
                    environment.getProperty(
                            "image.rides-service",
                            String.class
                    ),
                    "Rides service image is null"
            );

            final PostgreSQLContainer<?> RIDES_DB = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                    .withNetwork(SHARED_NETWORK)
                    .withNetworkAliases("ridedb-service")
                    .withDatabaseName("rides");

            Startables.deepStart(RIDES_DB).join();

            return new GenericContainer<>(apiServiceImage)
                    .withEnv("POSTGRES_DB", "rides")
                    .withEnv("spring.datasource.username", RIDES_DB.getUsername())
                    .withEnv("spring.datasource.password", RIDES_DB.getPassword())
                    .withEnv("spring.kafka.bootstrap-servers", "kafka:9092")
                    .withNetwork(SHARED_NETWORK)
                    .withNetworkAliases("ride-service");
        }

        private GenericContainer<?> createRatingsServiceContainer(Environment environment) {
            final var apiServiceImage = requireNonNull(
                    environment.getProperty(
                            "image.ratings-service",
                            String.class
                    ),
                    "Ratings service image is null"
            );

            final PostgreSQLContainer<?> RATINGS_DB = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                    .withNetwork(SHARED_NETWORK)
                    .withNetworkAliases("ratingdb-service")
                    .withDatabaseName("ratings");

            Startables.deepStart(RATINGS_DB).join();

            return new GenericContainer<>(apiServiceImage)
                    .withEnv("POSTGRES_DB", "ratings")
                    .withEnv("spring.datasource.username", RATINGS_DB.getUsername())
                    .withEnv("spring.datasource.password", RATINGS_DB.getPassword())
                    .withEnv("spring.kafka.bootstrap-servers", "kafka:9092")
                    .withNetwork(SHARED_NETWORK)
                    .withNetworkAliases("ratings-service");
        }

        private GenericContainer<?> createGatewayServiceContainer(Environment environment, int apiExposedPort) {
            final var gatewayServiceImage = requireNonNull(
                    environment.getProperty(
                            "image.gateway-service",
                            String.class
                    ),
                    "Gateway service image is null"
            );

            return new GenericContainer<>(gatewayServiceImage)
                    .withNetwork(SHARED_NETWORK)
                    .withExposedPorts(8080)
                    .withCreateContainerCmdModifier(
                            cmd -> cmd.withHostConfig(
                                    new HostConfig()
                                            .withNetworkMode(SHARED_NETWORK.getId())
                                            .withPortBindings(new PortBinding(
                                                    Ports.Binding.bindPort(apiExposedPort),
                                                    new ExposedPort(8080)
                                            ))
                            )
                    );
        }

        private GenericContainer<?> createEurekaServiceContainer(Environment environment) {
            final var eurekaServiceImage = requireNonNull(
                    environment.getProperty(
                            "image.eureka-service",
                            String.class
                    ),
                    "Eureka service image is null"
            );


            return new GenericContainer<>(eurekaServiceImage)
                    .withNetwork(SHARED_NETWORK)
                    .withNetworkAliases("eureka-server");
        }

        private void setPropertiesForConnections(ConfigurableEnvironment environment) {
            environment.getPropertySources().addFirst(
                    new MapPropertySource(
                            "testcontainers",
                            Map.of(
                                "api.host", PASSENGERS_SERVICE.getHost()
                            )
                    )
            );
        }
    }
}
