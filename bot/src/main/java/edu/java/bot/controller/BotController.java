package edu.java.bot.controller;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.requestDTO.LinkUpdateRequest;
import edu.java.bot.model.responseDTO.ApiErrorResponse;
import edu.java.bot.service.CustomTelegramBot;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BotController {
    @Autowired
    CustomTelegramBot telegramBot;

    @Autowired
    Bucket bucket;

    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {@ApiResponse(responseCode = "400",
                                       content = @Content(mediaType = "*/*",
                                       schema = @Schema(implementation = ApiErrorResponse.class)))})
    @PostMapping("/updates")
    public ResponseEntity<?> processUpdate(@Valid @RequestBody LinkUpdateRequest linkUpdateRequest) {
        if (bucket.tryConsume(1)) {
            for (Long chatId : linkUpdateRequest.tgChatIds()) {
                telegramBot.execute(new SendMessage(
                    chatId,
                    linkUpdateRequest.url().toString()
                        + "\n"
                        + linkUpdateRequest.description()
                ));
            }
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}

