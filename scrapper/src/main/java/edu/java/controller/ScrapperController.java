package edu.java.controller;

import edu.java.model.requestDTO.AddLinkRequest;
import edu.java.model.requestDTO.RemoveLinkRequest;
import edu.java.model.responseDTO.ApiErrorResponse;
import edu.java.model.responseDTO.LinkResponse;
import edu.java.model.responseDTO.ListLinksResponse;
import edu.java.service.LinkService;
import edu.java.service.TgChatService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScrapperController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private TgChatService tgChatService;

    @Autowired
    private Bucket bucket;

    @PostMapping("/links")
    public ResponseEntity<?> addLink(@RequestHeader("Tg-Chat-Id") Long tgChatId,
        @Valid @RequestBody AddLinkRequest request) {
        if (bucket.tryConsume(1)) {
            linkService.add(tgChatId, request.link());
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @GetMapping("/links")
    public ResponseEntity<ListLinksResponse> getAllLinks(@RequestHeader("Tg-Chat-Id") Long tgChatId) {
        if (bucket.tryConsume(1)) {
            List<LinkResponse> trackedLinks = linkService.listAll(tgChatId)
                .stream()
                .map(link -> new LinkResponse(link.id(), link.url()))
                .toList();

            ListLinksResponse response = new ListLinksResponse(trackedLinks, trackedLinks.size());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "404",
                     content = @Content(mediaType = "*/*",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/links")
    public ResponseEntity<?> removeLink(@RequestHeader("Tg-Chat-Id") Long tgChatId,
        @Valid @RequestBody RemoveLinkRequest request) {
        if (bucket.tryConsume(1)) {
            linkService.remove(tgChatId, request.link());
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                     content = @Content(mediaType = "*/*",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<?> registerChat(@PathVariable("id") Long id) {
        if (bucket.tryConsume(1)) {
            tgChatService.register(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404",
                     content = @Content(mediaType = "*/*",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<?> deleteChat(@PathVariable("id") Long id) {
        if (bucket.tryConsume(1)) {
            tgChatService.unregister(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
