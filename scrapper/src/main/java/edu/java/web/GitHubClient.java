package edu.java.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.util.ClientErrorCode;
import edu.java.util.ExceptionLogger;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

// We need to know owner and repository name
public class GitHubClient extends WebSiteClient {

    @Autowired
    ClientErrorCode errorCode;
    ExceptionLogger logger = new ExceptionLogger();


    @Autowired
    public GitHubClient(WebClient.Builder webClientBuilder) {
        super(webClientBuilder, "https://api.github.com");
    }

    @Override
    protected Pair<String, String> fetchData(Mono<String> mono) {
        return mono.map(jsonString -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(jsonString);

                String date = jsonNode.get("updated_at").asText();
                String identifier = jsonNode.get("id").asText();

                return Pair.of(date, identifier);
            } catch (Exception e) {
                String code = "EXIT_CODE_1";
                String errorMessage = errorCode.getClientErrorCode(code);
                logger.logRequest(code, errorMessage);
                return Pair.of("", "");
            }
        }).block();
    }

    @Override
    protected OffsetDateTime parseDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
            return OffsetDateTime.parse(date, formatter);
        } catch (Exception e) {
            String code = "EXIT_CODE_2";
            String errorMessage = errorCode.getClientErrorCode(code);
            logger.logRequest(code, errorMessage);
            return null;
        }
    }
}
