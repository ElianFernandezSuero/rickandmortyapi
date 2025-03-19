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

public class CharacterTests extends BaseTest {

    private static final String ENDPOINT = "/character";
    private static final int MAX_RESPONSE_TIME_MS = 2000;

    @Test
    public void testGetAllCharacters() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(SUCCESS_STATUS_CODE)
                .body("info", notNullValue())
                .body("info.count", greaterThan(0))
                .body("info.pages", greaterThan(0))
                .body("results", notNullValue())
                .body("results.size()", greaterThan(0))
                .extract().response();

        Map<String, Object> responseBody = response.jsonPath().getMap("$");
        List<Map<String, Object>> results = (List<Map<String, Object>>) responseBody.get("results");
        Map<String, Object> firstCharacter = results.get(0);

        assertTrue(firstCharacter.containsKey("id"), "Character is missing 'id' field");
        assertTrue(firstCharacter.containsKey("name"), "Character is missing 'name' field");
        assertTrue(firstCharacter.containsKey("status"), "Character is missing 'status' field");
        assertTrue(firstCharacter.containsKey("species"), "Character is missing 'species' field");
        assertTrue(firstCharacter.containsKey("gender"), "Character is missing 'gender' field");

        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }

    @Test
    public void testGetSingleCharacter() {
        int characterId = 1;

        Response response = given()
                .spec(requestSpec)
                .when()
                .get(ENDPOINT + "/" + characterId)
                .then()
                .statusCode(SUCCESS_STATUS_CODE) // Validate status code
                .body("id", equalTo(characterId))
                .body("name", equalTo("Rick Sanchez"))
                .body("status", notNullValue())
                .body("species", notNullValue())
                .body("type", notNullValue())
                .body("gender", notNullValue())
                .body("origin", notNullValue())
                .body("location", notNullValue())
                .body("image", notNullValue())
                .body("episode", notNullValue())
                .extract().response();

        Map<String, Object> character = response.jsonPath().getMap("$");
        assertEquals(((Integer)character.get("id")).intValue(), characterId, "Character ID does not match expected value");
        assertEquals(character.get("name"), "Rick Sanchez", "Character name does not match expected value");


        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }

    @Test
    public void testGetMultipleCharacters() {
        List<Integer> characterIds = Arrays.asList(1, 2, 3);

        Response response = given()
                .spec(requestSpec)
                .when()
                .get(ENDPOINT + "/" + characterIds.toString().replaceAll("[\\[\\]\\s]", ""))
                .then()
                .statusCode(SUCCESS_STATUS_CODE) // Validate status code
                .body("size()", equalTo(characterIds.size()))
                .extract().response();

        List<Map<String, Object>> characters = new ArrayList<>();
        for (int i = 0; i < characterIds.size(); i++) {
            Map<String, Object> character = response.jsonPath().getMap("[" + i + "]");
            characters.add(character);
        }

        assertEquals(characters.size(), characterIds.size(), "Number of returned characters does not match");

        for (int i = 0; i < characterIds.size(); i++) {
            Object idObj = characters.get(i).get("id");
            int id = (idObj instanceof Integer) ? (Integer)idObj : Integer.parseInt(idObj.toString());
            assertEquals(id, characterIds.get(i).intValue(),
                    "Character ID at index " + i + " does not match expected value");
        }

        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }

    @Test
    public void testGetNonExistentCharacter() {
        int nonExistentCharacterId = 999999;

        Response response = given()
                .spec(requestSpec)
                .when()
                .get(ENDPOINT + "/" + nonExistentCharacterId)
                .then()
                .statusCode(NOT_FOUND_STATUS_CODE) // Validate status code
                .body("error", equalTo("Character not found"))
                .extract().response();

        ApiUtils.verifyCommonResponseHeaders(response);
    }

    @Test
    public void testFilterCharacters() {
        String nameFilter = "rick";
        String statusFilter = "alive";

        Response response = given()
                .spec(requestSpec)
                .queryParam("name", nameFilter)
                .queryParam("status", statusFilter)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(SUCCESS_STATUS_CODE)
                .body("results", notNullValue())
                .extract().response();

        Map<String, Object> responseBody = response.jsonPath().getMap("$");
        List<Map<String, Object>> results = (List<Map<String, Object>>) responseBody.get("results");

        for (Map<String, Object> character : results) {
            String name = (String) character.get("name");
            String status = (String) character.get("status");

            assertTrue(name.toLowerCase().contains(nameFilter.toLowerCase()),
                    "Character name '" + name + "' does not contain filter '" + nameFilter + "'");
            assertEquals(status.toLowerCase(), statusFilter.toLowerCase(),
                    "Character status '" + status + "' does not match filter '" + statusFilter + "'");
        }

        // Verify response headers
        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }

    @Test
    public void testGetAllEpisodes() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(SUCCESS_STATUS_CODE)
                .body("info", notNullValue())
                .extract().response();

        ApiUtils.verifyCommonResponseHeaders(response);
        ApiUtils.verifyResponseTime(response, MAX_RESPONSE_TIME_MS);
    }
} 