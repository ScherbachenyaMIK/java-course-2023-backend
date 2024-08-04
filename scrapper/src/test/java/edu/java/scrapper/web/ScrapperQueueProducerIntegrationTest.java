package edu.java.scrapper.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.responseDTO.BotRequest;
import edu.java.web.ScrapperQueueProducer;
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
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class ScrapperQueueProducerIntegrationTest extends IntegrationTest {
    @Autowired
    private ScrapperQueueProducer scrapperQueueProducer;

    private KafkaConsumer<String, String> kafkaConsumer;

    @Test
    public void sendUpdate() throws JsonProcessingException {
        initKafkaConsumer();
        kafkaConsumer.subscribe(Collections.singletonList("topic1"));

        BotRequest botRequest = new BotRequest(
            1L,
            URI.create("http://github.com/"),
            "Testing",
            List.of(1L, 2L, 3L, 4L, 5L)
        );
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBotRequest = objectMapper.writeValueAsString(botRequest);

        scrapperQueueProducer.sendUpdate(botRequest);

        await()
            .atMost(5, SECONDS)
            .untilAsserted(
                () -> assertThat(checkKafkaConsumer().value())
                    .isEqualTo(jsonBotRequest)
            );
    }

    private void initKafkaConsumer() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
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
