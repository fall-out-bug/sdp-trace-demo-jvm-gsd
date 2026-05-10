package com.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers

/**
 * Integration tests for the health endpoint.
 * 
 * Tests verify:
 * 1. Health endpoint returns 200 OK
 * 2. Health endpoint returns application/json Content-Type
 * 3. Health response includes status = "ok"
 * 4. Health response includes timestamp field
 */
class AppTest {

    @Test
    fun `health endpoint returns 200 OK`() {
        // Given: A running server
        val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
        val client = HttpClient.newHttpClient()
        val uri = URI("http://localhost:$port/health")
        
        // When: Request is made to health endpoint
        val request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build()
        
        val response = client.send(request, BodyHandlers.ofString())
        
        // Then: Status code should be 200
        assertEquals(200, response.statusCode(), "Health endpoint should return 200 OK")
    }

    @Test
    fun `health endpoint returns application/json`() {
        // Given: A running server
        val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
        val client = HttpClient.newHttpClient()
        val uri = URI("http://localhost:$port/health")
        
        // When: Request is made to health endpoint
        val request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build()
        
        val response = client.send(request, BodyHandlers.ofString())
        
        // Then: Content-Type should be application/json
        val contentType = response.headers().firstValue("Content-Type").orElse("")
        assertEquals("application/json", contentType, "Health endpoint should return JSON")
    }

    @Test
    fun `health response includes status and timestamp`() {
        // Given: A running server
        val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
        val client = HttpClient.newHttpClient()
        val uri = URI("http://localhost:$port/health")
        
        // When: Request is made to health endpoint
        val request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build()
        
        val response = client.send(request, BodyHandlers.ofString())
        val body = response.body()
        
        // Then: Response should contain status and timestamp
        assert(body.contains("\"status\""), "Response should contain status field")
        assert(body.contains("\"timestamp\""), "Response should contain timestamp field")
        assert(body.contains("\"ok\""), "Status should be 'ok'")
    }
}