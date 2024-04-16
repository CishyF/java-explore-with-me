package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import ru.practicum.dto.EndpointHitDtoRequest;
import ru.practicum.util.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StatsClient extends BaseClient {

    public StatsClient(@Value("${stats-service.url}") String serviceUri) {
        super(
                WebClient.builder()
                        .uriBuilderFactory(new DefaultUriBuilderFactory(serviceUri))
                        .clientConnector(new ReactorClientHttpConnector(HttpClient.create(ConnectionProvider.newConnection())))
                        .build()
        );
    }

    public ResponseEntity<Object> createEndpointHit(EndpointHitDtoRequest dto) {
        log.info("Пришел POST-запрос stats-client/hit с телом={}", dto);
        ResponseEntity<Object> response = post("/hit", null, dto);
        log.info("Ответ на POST-запрос stats-client/hit с телом={}", dto);
        return response;
    }

    public ResponseEntity<Object> getViewStats(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris) {
        String encodedStartTime = start.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN));
        String encodedEndTime = end.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN));
        String urisString = uris == null ? "" : String.join(",", uris);
        Map<String, Object> parameters = Map.of(
                "start", encodedStartTime,
                "end", encodedEndTime,
                "unique", unique,
                "uris", urisString
        );
        log.info("Пришел GET-запрос stats-client/stats?start={}&end={}&unique={}&uris={}", encodedStartTime, encodedEndTime, unique, urisString);
        ResponseEntity<Object> response = get("/stats", parameters);
        log.info("Ответ на GET-запрос stats-client/stats?start={}&end={}&unique={}&uris={} с телом={}", encodedStartTime, encodedEndTime, unique, urisString, response);
        return response;
    }
}
