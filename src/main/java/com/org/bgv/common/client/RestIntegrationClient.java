package com.org.bgv.common.client;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RestIntegrationClient implements IntegrationClient {

    private final RestClient restClient = RestClient.create();

    @Override
    public <REQ, RES> RES post(
            String baseUrl,
            String path,
            Map<String, String> headers,
            REQ request,
            Class<RES> responseType) {

        RestClient.RequestBodySpec spec = restClient.post()
                .uri(baseUrl + path);

        headers.forEach(spec::header);

        return spec.body(request)
                .retrieve()
                .body(responseType);
    }

    @Override
    public <RES> RES get(
            String baseUrl,
            String path,
            Map<String, String> headers,
            Class<RES> responseType) {

        RestClient.RequestHeadersSpec<?> spec = restClient.get()
                .uri(baseUrl + path);

        headers.forEach(spec::header);

        return spec.retrieve()
                .body(responseType);
    }
}
