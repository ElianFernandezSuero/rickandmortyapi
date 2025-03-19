package com.rickandmorty.tests;

import com.beust.jcommander.internal.Console;
import com.rickandmorty.utils.ApiUtils;
import com.rickandmorty.utils.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LocationTests extends BaseTest {

    private static final String ENDPOINT = "/location";
    private static final int MAX_RESPONSE_TIME_MS = 2000;

    @Test
    public void testGetAllLocations() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(SUCCESS_STATUS_CODE) // Validate status code
                .body("info", notNullValue())
                .body("info.count", greaterThan(0))
                .body("info.pages", greaterThan(0))
                .body("results", notNullValue())
                .body("results.size()", greaterThan(0))
                .extract().response();

        Map<String, Object> responseBody = response.jsonPath().getMap("$");
        List<Map<String, Object>> results = (List<Map<String, Object>>) responseBody.get("results");
        System.out.println(results);
        Map<String, Object> firstLocation = results.get(0);

        assertTrue(firstLocation.containsKey("id"), "Location is missing 'id' field");
        assertTrue(firstLocation.containsKey("name"), "Location is missing 'name' field");
        assertTrue(firstLocation.containsKey("type"), "Location is missing 'type' field");
        assertTrue(firstLocation.containsKey("dimension"), "Location is missing 'dimension' field");
        assertTrue(firstLocation.containsKey("residents"), "Location is missing 'residents' field");

        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }

    @Test
    public void testGetSingleLocation() {
        int locationId = 1;

        Response response = given()
                .spec(requestSpec)
                .when()
                .get(ENDPOINT + "/" + locationId)
                .then()
                .statusCode(SUCCESS_STATUS_CODE) // Validate status code
                .body("id", equalTo(locationId))
                .body("name", equalTo("Earth (C-137)"))
                .body("type", notNullValue())
                .body("dimension", notNullValue())
                .body("residents", notNullValue())
                .extract().response();

        Map<String, Object> location = response.jsonPath().getMap("$");
        assertEquals(((Integer)location.get("id")).intValue(), locationId, "Location ID does not match expected value");
        assertEquals(location.get("name"), "Earth (C-137)", "Location name does not match expected value");

        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }

    @Test
    public void testGetMultipleLocations() {
        List<Integer> locationIds = Arrays.asList(1, 2, 3);

        Response response = given()
                .spec(requestSpec)
                .when()
                .get(ENDPOINT + "/" + locationIds.toString().replaceAll("[\\[\\]\\s]", ""))
                .then()
                .statusCode(SUCCESS_STATUS_CODE) // Validate status code
                .body("size()", equalTo(locationIds.size()))
                .extract().response();

        List<Map<String, Object>> locations = new ArrayList<>();
        for (int i = 0; i < locationIds.size(); i++) {
            Map<String, Object> location = response.jsonPath().getMap("[" + i + "]");
            locations.add(location);
        }

        assertEquals(locations.size(), locationIds.size(), "Number of returned locations does not match");

        for (int i = 0; i < locationIds.size(); i++) {
            Object idObj = locations.get(i).get("id");
            int id = (idObj instanceof Integer) ? (Integer)idObj : Integer.parseInt(idObj.toString());
            assertEquals(id, locationIds.get(i).intValue(),
                    "Location ID at index " + i + " does not match expected value");
        }

        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }

    @Test
    public void testGetNonExistentLocation() {
        int nonExistentLocationId = 999999;

        Response response = given()
                .spec(requestSpec)
                .when()
                .get(ENDPOINT + "/" + nonExistentLocationId)
                .then()
                .statusCode(NOT_FOUND_STATUS_CODE) // Validate status code
                .body("error", equalTo("Location not found"))
                .extract().response();

        ApiUtils.verifyCommonResponseHeaders(response);
    }

    @Test
    public void testFilterLocations() {
        String nameFilter = "earth";
        String typeFilter = "planet";

        Response response = given()
                .spec(requestSpec)
                .queryParam("name", nameFilter)
                .queryParam("type", typeFilter)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(SUCCESS_STATUS_CODE)
                .body("results", notNullValue())
                .extract().response();

        Map<String, Object> responseBody = response.jsonPath().getMap("$");
        List<Map<String, Object>> results = (List<Map<String, Object>>) responseBody.get("results");

        for (Map<String, Object> location : results) {
            String name = (String) location.get("name");
            String type = (String) location.get("type");

            assertTrue(name.toLowerCase().contains(nameFilter.toLowerCase()),
                    "Location name '" + name + "' does not contain filter '" + nameFilter + "'");
            assertEquals(type.toLowerCase(), typeFilter.toLowerCase(),
                    "Location type '" + type + "' does not match filter '" + typeFilter + "'");
        }

        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }
} 