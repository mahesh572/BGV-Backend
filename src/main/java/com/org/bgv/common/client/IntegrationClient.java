package com.org.bgv.common.client;

import java.util.Map;

public interface IntegrationClient {

    <REQ, RES> RES post(
            String baseUrl,
            String path,
            Map<String, String> headers,
            REQ request,
            Class<RES> responseType);

    <RES> RES get(
            String baseUrl,
            String path,
            Map<String, String> headers,
            Class<RES> responseType);
}
