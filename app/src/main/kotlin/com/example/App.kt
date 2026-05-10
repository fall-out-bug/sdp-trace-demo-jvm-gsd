package com.example

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.time.Instant

fun main(args: Array<String>) {
    val port = getPort()
    val server = HttpServer.create(InetSocketAddress(port), 0)
    
    server.createContext("/health") { exchange ->
        try {
            val response = HealthResponse.ok()
            val jsonResponse = """{"status":"${escapeJson(response.status)}","timestamp":${response.timestamp}}"""
            exchange.responseHeaders.set("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, jsonResponse.length.toLong())
            val os: OutputStream = exchange.responseBody
            os.write(jsonResponse.toByteArray(StandardCharsets.UTF_8))
            os.close()
        } catch (e: Exception) {
            exchange.sendResponseHeaders(500, 0)
            exchange.close()
        }
    }
    
    Runtime.getRuntime().addShutdownHook(Thread {
        println("Shutting down server...")
        server.stop(10)
    })
    server.start()
    println("Server started on port $port")
    println("Health endpoint: http://localhost:$port/health")
}

fun escapeJson(s: String): String = s
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")
    .replace("\n", "\\n")
    .replace("\r", "\\r")
    .replace("\t", "\\t")

fun getPort(): Int {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    require(port > 0 && port <= 65535) { "PORT must be between 1 and 65535, got: $port" }
    return port
}