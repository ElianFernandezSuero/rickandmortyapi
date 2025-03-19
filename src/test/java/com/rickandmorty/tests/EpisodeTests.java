package com.rickandmorty.tests;

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

public class EpisodeTests extends BaseTest {

    private static final String ENDPOINT = "/episode";
    private static final int MAX_RESPONSE_TIME_MS = 2000;

    @Test
    public void testGetAllEpisodes() {
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
        Map<String, Object> firstEpisode = results.get(0);

        assertTrue(firstEpisode.containsKey("id"), "Episode is missing 'id' field");
        assertTrue(firstEpisode.containsKey("name"), "Episode is missing 'name' field");
        assertTrue(firstEpisode.containsKey("air_date"), "Episode is missing 'air_date' field");
        assertTrue(firstEpisode.containsKey("episode"), "Episode is missing 'episode' field");
        assertTrue(firstEpisode.containsKey("characters"), "Episode is missing 'characters' field");

        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }

    @Test
    public void testGetSingleEpisode() {
        int episodeId = 1;

        Response response = given()
                .spec(requestSpec)
                .when()
                .get(ENDPOINT + "/" + episodeId)
                .then()
                .statusCode(SUCCESS_STATUS_CODE)
                .body("id", equalTo(episodeId))
                .body("name", equalTo("Pilot"))
                .body("air_date", notNullValue())
                .body("episode", notNullValue())
                .body("characters", notNullValue())
                .extract().response();

        Map<String, Object> episode = response.jsonPath().getMap("$");
        assertEquals(((Integer)episode.get("id")).intValue(), episodeId, "Episode ID does not match expected value");
        assertEquals(episode.get("name"), "Pilot", "Episode name does not match expected value");

        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }

    @Test
    public void testGetMultipleEpisodes() {
        List<Integer> episodeIds = Arrays.asList(1, 2, 3);

        Response response = given()
                .spec(requestSpec)
                .when()
                .get(ENDPOINT + "/" + episodeIds.toString().replaceAll("[\\[\\]\\s]", ""))
                .then()
                .statusCode(SUCCESS_STATUS_CODE)
                .body("size()", equalTo(episodeIds.size()))
                .extract().response();

        List<Map<String, Object>> episodes = new ArrayList<>();
        for (int i = 0; i < episodeIds.size(); i++) {
            Map<String, Object> episode = response.jsonPath().getMap("[" + i + "]");
            episodes.add(episode);
        }

        assertEquals(episodes.size(), episodeIds.size(), "Number of returned episodes does not match");

        for (int i = 0; i < episodeIds.size(); i++) {
            Object idObj = episodes.get(i).get("id");
            int id = (idObj instanceof Integer) ? (Integer)idObj : Integer.parseInt(idObj.toString());
            assertEquals(id, episodeIds.get(i).intValue(),
                    "Episode ID at index " + i + " does not match expected value");
        }

        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }

    @Test
    public void testGetNonExistentEpisode() {
        int nonExistentEpisodeId = 999999;

        Response response = given()
                .spec(requestSpec)
                .when()
                .get(ENDPOINT + "/" + nonExistentEpisodeId)
                .then()
                .statusCode(NOT_FOUND_STATUS_CODE) // Validate status code
                .body("error", equalTo("Episode not found"))
                .extract().response();

        ApiUtils.verifyCommonResponseHeaders(response);
    }

    @Test
    public void testFilterEpisodes() {
        String nameFilter = "pilot";

        Response response = given()
                .spec(requestSpec)
                .queryParam("name", nameFilter)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(SUCCESS_STATUS_CODE)
                .body("results", notNullValue())
                .extract().response();

        Map<String, Object> responseBody = response.jsonPath().getMap("$");
        List<Map<String, Object>> results = (List<Map<String, Object>>) responseBody.get("results");

        for (Map<String, Object> episode : results) {
            String name = (String) episode.get("name");

            assertTrue(name.toLowerCase().contains(nameFilter.toLowerCase()),
                    "Episode name '" + name + "' does not contain filter '" + nameFilter + "'");
        }

        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }

    @Test
    public void testEpisodeCodeFormat() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(SUCCESS_STATUS_CODE)
                .extract().response();

        Map<String, Object> responseBody = response.jsonPath().getMap("$");
        List<Map<String, Object>> results = (List<Map<String, Object>>) responseBody.get("results");

        for (Map<String, Object> episode : results) {
            String episodeCode = (String) episode.get("episode");

            assertTrue(episodeCode.matches("S\\d{2}E\\d{2}"),
                    "Episode code '" + episodeCode + "' does not follow the expected format 'SxxExx'");
        }

        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }
} 