package com.rickandmorty.utils;

import io.restassured.response.Response;

import java.util.Map;

public class ApiUtils {

    public static final String EXPECTED_CONTENT_TYPE = "application/json; charset=utf-8";

    public static void verifyCommonResponseHeaders(Response response) {
        assert response.getHeader("Content-Type").equals(EXPECTED_CONTENT_TYPE) : 
            "Expected Content-Type: " + EXPECTED_CONTENT_TYPE + ", but got: " + response.getHeader("Content-Type");

        assert response.getHeader("Server") != null : "Server header is missing";
    }

    public static void verifyPaginationInfo(Map<String, Object> responseBody) {
        assert responseBody.containsKey("info") : "Pagination info is missing";
        
        Map<String, Object> info = (Map<String, Object>) responseBody.get("info");
        assert info.containsKey("count") : "Count field is missing in pagination info";
        assert info.containsKey("pages") : "Pages field is missing in pagination info";
        assert info.containsKey("next") : "Next field is missing in pagination info";
        assert info.containsKey("prev") : "Prev field is missing in pagination info";
    }

    /**
     * @param response Response object
     * @param maxResponseTimeMs response time
     */
    public static void verifyResponseTime(Response response, long maxResponseTimeMs) {
        long responseTimeMs = response.getTime();
        assert responseTimeMs <= maxResponseTimeMs : 
            "Response time " + responseTimeMs + "ms exceeds maximum acceptable time " + maxResponseTimeMs + "ms";
    }
} 