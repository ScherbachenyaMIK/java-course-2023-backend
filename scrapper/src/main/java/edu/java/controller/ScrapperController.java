package edu.java.controller;

import edu.java.exception.LinkNotFoundException;
import edu.java.exception.NoSuchUserRegisteredException;
import edu.java.exception.UserAlreadyRegisteredException;
import edu.java.model.requestDTO.AddLinkRequest;
import edu.java.model.requestDTO.RemoveLinkRequest;
import edu.java.model.responseDTO.ApiErrorResponse;
import edu.java.model.responseDTO.LinkResponse;
import edu.java.model.responseDTO.ListLinksResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
public class ScrapperController {

    @PostMapping("/links")
    public ResponseEntity<?> addLink(@RequestHeader("Tg-Chat-Id") Long tgChatId,
            @Valid @RequestBody AddLinkRequest request) {
        log.info("Request add link registered");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/links")
    public ResponseEntity<ListLinksResponse> getAllLinks(@RequestHeader("Tg-Chat-Id") Long tgChatId) {
        List<LinkResponse> trackedLinks = new ArrayList<>();
        try {
            trackedLinks.add(new LinkResponse(
                1L,
                new URI("https://github.com/ScherbachenyaMIK/java-course-2023-backend")));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        ListLinksResponse response = new ListLinksResponse(trackedLinks, trackedLinks.size());
        log.info("Request get links registered");
        return ResponseEntity.ok(response);
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "404",
                     content = @Content(mediaType = "*/*",
                     schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/links")
    public ResponseEntity<?> removeLink(@RequestHeader("Tg-Chat-Id") Long tgChatId,
            @Valid @RequestBody RemoveLinkRequest request) throws URISyntaxException {
        URI linkToRemove = request.link();
        List<LinkResponse> trackedLinks = new ArrayList<>();
        trackedLinks.add(new LinkResponse(1L, new URI("https://api.stackexchange.com/2.2/questions"
            + "/78110387?order=desc&sort=activity&site=stackoverflow")));
        boolean removed = trackedLinks.removeIf(link -> link.url().equals(linkToRemove));
        log.info("Request remove link registered");
        if (removed) {
            return ResponseEntity.ok().build();
        } else {
            throw new LinkNotFoundException();
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                     content = @Content(mediaType = "*/*",
                     schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<?> registerChat(@PathVariable("id") Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        boolean exist = ids.contains(id);
        log.info("Request register chat registered");
        if (!exist) {
            return ResponseEntity.ok().build();
        } else {
            throw new UserAlreadyRegisteredException();
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404",
                     content = @Content(mediaType = "*/*",
                     schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<?> deleteChat(@PathVariable("id") Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        boolean removed = ids.removeIf(curID -> curID.equals(id));
        log.info("Request delete chat registered");
        if (removed) {
            return ResponseEntity.ok().build();
        } else {
            throw new NoSuchUserRegisteredException();
        }
    }
}
