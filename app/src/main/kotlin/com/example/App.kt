package com.example

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.io.OutputStream
import java.nio.charset.StandardCharsets

fun main(args: Array<String>) {
    val port = getPort()
    val server = HttpServer.create(InetSocketAddress(port), 0)
    
    server.createContext("/health") { exchange ->
        val response = HealthResponse.ok()
        // Manual JSON serialization since we can't use kotlinx-serialization easily with Bazel
        val jsonResponse = """{"status":"${response.status}","timestamp":${response.timestamp}}"""
        exchange.responseHeaders.set("Content-Type", "application/json")
        exchange.sendResponseHeaders(200, jsonResponse.length.toLong())
        val os: OutputStream = exchange.responseBody
        os.write(jsonResponse.toByteArray(StandardCharsets.UTF_8))
        os.close()
    }
    
    server.start()
    println("Server started on port $port")
    println("Health endpoint: http://localhost:$port/health")
}

fun getPort(): Int {
    return System.getenv("PORT")?.toIntOrNull() ?: 8080
}