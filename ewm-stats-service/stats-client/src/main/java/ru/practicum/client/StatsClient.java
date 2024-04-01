package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDtoRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
public class StatsClient extends BaseClient {

    public StatsClient(@Value("${stats-service.uri}") String serviceUri, WebClient webClient) {
        super(
                WebClient.builder()
                        .baseUrl(serviceUri)
                        .uriBuilderFactory(new DefaultUriBuilderFactory(serviceUri))
                        .build()
        );
    }

    public ResponseEntity<Object> createEndpointHit(EndpointHitDtoRequest dto) {
        log.info("Пришел POST-запрос stats-client/hit с телом={}", dto);
        ResponseEntity<Object> response = post("/hit", dto);
        log.info("Ответ на POST-запрос stats-client/hit с телом={}", dto);
        return response;
    }

    public ResponseEntity<Object> getViewStats(LocalDateTime start, LocalDateTime end, Boolean unique) {
        String encodedStartTime = URLEncoder.encode(start.toString(), StandardCharsets.UTF_8);
        String endStartTime = URLEncoder.encode(end.toString(), StandardCharsets.UTF_8);
        Map<String, Object> parameters = Map.of(
                "start", encodedStartTime,
                "end", endStartTime,
                "unique", unique
        );
        log.info("Пришел GET-запрос stats-client/stats?start={}&end={}&unique={}", start, end, unique);
        ResponseEntity<Object> response = get("/stats?start={start}&end={end}&unique={unique}", parameters);
        log.info("Ответ на GET-запрос stats-client/stats?start={}&end={}&unique={} с телом={}", start, end, unique, response);
        return response;
    }
}
