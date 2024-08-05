package edu.java.scrapper.controller;

import edu.java.controller.Listener_dlq;
import edu.java.responseDTO.BotRequest;
import edu.java.scrapper.web.IntegrationTest;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class Listener_dlqIntegrationTest extends IntegrationTest {
    @Mock
    private Logger log;

    @Autowired
    private Listener_dlq listenerDlq;

    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void listen() {
        replaceLoggerWithMock();
        initKafkaProducer();
        BotRequest botRequest = new BotRequest(
            1L,
            URI.create("http://github.com/"),
            "Testing",
            List.of(1L, 2L, 3L, 4L, 5L)
        );

        kafkaTemplate.send("topic1_dlq", botRequest.toString());

        await()
            .atMost(5, SECONDS)
            .untilAsserted(
                () -> {
                    verify(log).debug(any());
                }
            );
    }

    private void initKafkaProducer() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    private void replaceLoggerWithMock() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(listenerDlq, "log", log);
    }
}
