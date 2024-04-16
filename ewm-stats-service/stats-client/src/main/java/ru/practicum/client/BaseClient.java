package ru.practicum.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public class BaseClient {

    protected final WebClient webClient;

    public BaseClient(WebClient webClient) {
        this.webClient = webClient;
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, parameters, body);
    }

    protected <T> ResponseEntity<Object> put(String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, parameters, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, parameters, body);
    }

    protected ResponseEntity<Object> delete(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body) {
        if (parameters != null) {
            path = addRequestParameters(path, parameters);
        }

        ResponseEntity<Object> ewmStatsServiceResponse;
        try {
            if (body != null) {
                ewmStatsServiceResponse = webClient.method(method)
                        .uri(path)
                        .bodyValue(body)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntity(Object.class)
                        .block();
            } else {
                ewmStatsServiceResponse = webClient.method(method)
                        .uri(path)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntity(Object.class)
                        .block();
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(ewmStatsServiceResponse);
    }

    private static String addRequestParameters(String path, Map<String, Object> parameters) {
        StringBuilder uriStringBuilder = new StringBuilder(path);
        uriStringBuilder.append("?");
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            uriStringBuilder.append(parameter.getKey());
            uriStringBuilder.append("=");
            uriStringBuilder.append(parameter.getValue());
            uriStringBuilder.append("&");
        }
        return uriStringBuilder.substring(0, uriStringBuilder.length() - 1);
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
