package edu.java.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.util.ClientErrorCode;
import edu.java.util.ExceptionLogger;
import edu.java.util.UserResponse;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

// We need to know only question id
public class StackOverflowClient extends WebSiteClient {

    @Autowired
    ClientErrorCode errorCode;
    ExceptionLogger logger = new ExceptionLogger();

    @Autowired
    public StackOverflowClient(WebClient.Builder webClientBuilder) {
        super(webClientBuilder, "https://api.stackexchange.com/2.3");
    }

    @Override
    public List<UserResponse> getResponse() {
        List<UserResponse> responses = new ArrayList<>();
        for (var path : pathList) {
            Pair<String, String> pair = fetchData(getMono(
                path + "?order=desc&sort=activity&site=stackoverflow"));
            responses.add(new UserResponse(parseDate(pair.getLeft()), pair.getRight()));
        }
        return responses;
    }

    @Override
    protected Pair<String, String> fetchData(Mono<String> mono) {
        return mono.map(jsonString -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(jsonString);

                JsonNode itemsNode = jsonNode.get("items");
                if (itemsNode != null && itemsNode.isArray() && !itemsNode.isEmpty()) {
                    JsonNode itemNode = itemsNode.get(0);
                    String date = itemNode.get("last_activity_date").asText();
                    String identifier = itemNode.get("question_id").asText();
                    return Pair.of(date, identifier);
                } else {
                    return Pair.of("", "");
                }
            } catch (Exception e) {
                String code = "EXIT_CODE_3";
                String errorMessage = errorCode.getClientErrorCode(code);
                logger.logRequest(code, errorMessage);
                return Pair.of("", "");
            }
        }).block();
    }

    @Override
    protected OffsetDateTime parseDate(String date) {
        try {
            long unixTimestamp = Long.parseLong(date);
            return OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(unixTimestamp), java.time.ZoneOffset.UTC);
        } catch (NumberFormatException e) {
            String code = "EXIT_CODE_2";
            String errorMessage = errorCode.getClientErrorCode(code);
            logger.logRequest(code, errorMessage);
            return null;
        }
    }
}
