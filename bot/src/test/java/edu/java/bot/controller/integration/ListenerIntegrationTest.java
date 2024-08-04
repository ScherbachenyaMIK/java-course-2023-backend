package edu.java.bot.controller.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.controller.IntegrationTest;
import edu.java.bot.controller.Listener;
import edu.java.bot.model.requestDTO.LinkUpdateRequest;
import edu.java.bot.service.MessageSender;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ListenerIntegrationTest extends IntegrationTest {
    @MockBean
    private MessageSender messageSender;

    @Autowired
    private Listener kafkaListener;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private KafkaConsumer<String, String> kafkaConsumer;

    @Test
    public void listen() throws JsonProcessingException {
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(
            1L,
            URI.create("http://github.com/"),
            "Testing",
            List.of(1L, 2L, 3L, 4L, 5L)
        );
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonLinkUpdateRequest = objectMapper.writeValueAsString(linkUpdateRequest);

        kafkaTemplate.send("topic1", jsonLinkUpdateRequest);

        await()
            .atMost(5, SECONDS)
            .untilAsserted(
                () -> verify(messageSender, times(1))
                    .sendMessage(any())
            );
    }

    @Test
    public void listenThrowsException() throws JsonProcessingException {
        initKafkaConsumer();
        kafkaConsumer.subscribe(Collections.singletonList("topic1_dlq"));

        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(
            1L,
            URI.create("http://ErrorLink.com/"),
            "ErrorTesting",
            List.of(1L, 2L, 3L, 4L, 5L)
        );
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonLinkUpdateRequest = objectMapper.writeValueAsString(linkUpdateRequest);

        doThrow(new IllegalArgumentException("You shouldn't push updates on ErrorLink!"))
            .when(messageSender).sendMessage(any());

        kafkaTemplate.send("topic1", jsonLinkUpdateRequest);

        await()
            .atMost(5, SECONDS)
            .ignoreExceptions()
            .untilAsserted(
                () -> verify(messageSender, times(1))
                    .sendMessage(any())
            );

        await()
            .atMost(5, SECONDS)
            .untilAsserted(
                () -> assertThat(checkKafkaConsumer().value())
                    .isEqualTo(linkUpdateRequest.toString())
            );
    }

    private void initKafkaConsumer() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group2");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        kafkaConsumer = new KafkaConsumer<>(props);
    }

    private ConsumerRecord<String, String> checkKafkaConsumer() {
        while (true) {
            ConsumerRecords<String, String> records =
                kafkaConsumer.poll(Duration.ofMillis(1000));
            if (!records.isEmpty()) {
                assert(records.count() == 1);
                return records.iterator().next();
            }
        }
    }
}
