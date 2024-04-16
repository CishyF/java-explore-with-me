package ru.practicum.stats;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDtoRequest;
import ru.practicum.dto.ViewStatsDtoResponse;
import ru.practicum.event.entity.Event;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsProxyImpl implements StatsProxy {

    private static final String EVENT_PREFIX = "/events/";
    private static final String APP_PREFIX = "ewm-main-service";
    private final StatsClient statsClient;

    @Override
    public long getViews(Event event) {
        ResponseEntity<Object> response = statsClient.getViewStats(
                event.getCreatedOn(),
                LocalDateTime.now(),
                true,
                Collections.singletonList(EVENT_PREFIX + event.getId())
        );
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        try {
            TypeReference<List<ViewStatsDtoResponse>> typeRef = new TypeReference<List<ViewStatsDtoResponse>>() {};
            String responseBody = response.getBody().toString().replace("=", ":\"")
                    .replace(",", "\",")
                    .replace("}", "\"}");
            List<ViewStatsDtoResponse> views = mapper.readValue(responseBody, typeRef);
            return views.size();
        } catch (Exception e) {
            log.error("Ошибка при получении количество просмотров эндпоинта", e);
            return 0L;
        }
    }

    @Override
    public void addHit(String uri, String ip) {
        EndpointHitDtoRequest dto = EndpointHitDtoRequest.builder()
                        .app(APP_PREFIX)
                        .uri(uri)
                        .ip(ip)
                        .timestamp(LocalDateTime.now())
                        .build();
        statsClient.createEndpointHit(dto);
    }
}
