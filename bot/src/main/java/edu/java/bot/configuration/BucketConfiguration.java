package edu.java.bot.configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "bucket4j.config")
public class BucketConfiguration {
    private long tokenLimit;

    private long refillCount;

    private Duration refillDuration;

    @Bean
    Bucket bucket() {
        Bandwidth limit = Bandwidth.classic(tokenLimit, Refill.intervally(refillCount, refillDuration));
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
}
