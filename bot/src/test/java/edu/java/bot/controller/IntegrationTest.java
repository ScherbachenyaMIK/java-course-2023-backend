package edu.java.bot.controller;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class IntegrationTest {
    private static final Network network = Network.newNetwork();
    public static GenericContainer<?> ZOOKEEPER;
    public static KafkaContainer KAFKA;

    static {
        ZOOKEEPER = new GenericContainer<>(DockerImageName
            .parse("confluentinc/cp-zookeeper:7.3.2")
        )
            .withExposedPorts(2181)
            .withNetwork(network)
            .withNetworkAliases("zookeeper")
            .withEnv("ZOOKEEPER_CLIENT_PORT", "2181")
            .withEnv("ZOOKEEPER_SERVER_ID", "1")
            .withEnv("ZOOKEEPER_SERVERS", "zookeeper:2888:3888");

        ZOOKEEPER.start();

        String zookeeperAddress = ZOOKEEPER.getHost();
        Integer zookeeperPort = ZOOKEEPER.getExposedPorts().getFirst();

        KAFKA = new KafkaContainer(DockerImageName
            .parse("confluentinc/cp-kafka:7.3.2")
        )
            .withNetwork(network)
            .withNetworkAliases("kafka")
            .withExternalZookeeper(zookeeperAddress + ":" + zookeeperPort);

        KAFKA.start();
    }

    @DynamicPropertySource
    static void KafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }
}
