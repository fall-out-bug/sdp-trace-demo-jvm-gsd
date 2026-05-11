package com.example

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.io.OutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.time.Instant

val todoStore = TodoStore()

fun main(args: Array<String>) {
    val port = getPort()
    val server = HttpServer.create(InetSocketAddress(port), 0)
    
    server.createContext("/health") { exchange ->
        try {
            val response = HealthResponse.ok()
            val jsonResponse = """{"status":"${escapeJson(response.status)}","timestamp":${response.timestamp}}"""
            exchange.responseHeaders.set("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, jsonResponse.toByteArray(StandardCharsets.UTF_8).size.toLong())
            val os: OutputStream = exchange.responseBody
            os.write(jsonResponse.toByteArray(StandardCharsets.UTF_8))
            os.close()
        } catch (e: Exception) {
            exchange.sendResponseHeaders(500, 0)
            exchange.close()
        }
    }

    server.createContext("/ready") { exchange ->
        try {
            val jsonResponse = """{"ready":true}"""
            exchange.responseHeaders.set("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, jsonResponse.toByteArray(StandardCharsets.UTF_8).size.toLong())
            val os: OutputStream = exchange.responseBody
            os.write(jsonResponse.toByteArray(StandardCharsets.UTF_8))
            os.close()
        } catch (e: Exception) {
            exchange.sendResponseHeaders(500, 0)
            exchange.close()
        }
    }

    server.createContext("/live") { exchange ->
        try {
            val jsonResponse = """{"live":true}"""
            exchange.responseHeaders.set("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, jsonResponse.toByteArray(StandardCharsets.UTF_8).size.toLong())
            val os: OutputStream = exchange.responseBody
            os.write(jsonResponse.toByteArray(StandardCharsets.UTF_8))
            os.close()
        } catch (e: Exception) {
            exchange.sendResponseHeaders(500, 0)
            exchange.close()
        }
    }

    server.createContext("/todos") { exchange ->
        try {
            val method = exchange.requestMethod
            if (method == "GET") {
                val todos = todoStore.list()
                val json = todos.joinToString(",", "[", "]") { todoToJson(it) }
                exchange.responseHeaders.set("Content-Type", "application/json")
                exchange.sendResponseHeaders(200, json.toByteArray(StandardCharsets.UTF_8).size.toLong())
                val os = exchange.responseBody
                os.write(json.toByteArray(StandardCharsets.UTF_8))
                os.close()
            } else if (method == "POST") {
                val body = exchange.requestBody.readBytes().toString(StandardCharsets.UTF_8)
                val title = parseTitle(body)
                if (title.isNullOrBlank()) {
                    exchange.sendResponseHeaders(400, 0)
                    exchange.close()
                } else {
                    val todo = todoStore.create(title)
                    val json = todoToJson(todo)
                    exchange.responseHeaders.set("Content-Type", "application/json")
                    exchange.sendResponseHeaders(201, json.toByteArray(StandardCharsets.UTF_8).size.toLong())
                    val os = exchange.responseBody
                    os.write(json.toByteArray(StandardCharsets.UTF_8))
                    os.close()
                }
            } else {
                exchange.sendResponseHeaders(405, 0)
                exchange.close()
            }
        } catch (e: Exception) {
            exchange.sendResponseHeaders(500, 0)
            exchange.close()
        }
    }
    
    server.createContext("/todos/") { exchange ->
        try {
            val path = exchange.requestURI.path
            if (!path.startsWith("/todos/")) {
                exchange.sendResponseHeaders(404, 0)
                exchange.close()
                return@createContext
            }
            val rest = path.substring("/todos/".length)
            val parts = rest.split("/", limit = 2)
            if (parts.isEmpty() || parts[0].isEmpty()) {
                exchange.sendResponseHeaders(404, 0)
                exchange.close()
                return@createContext
            }
            if (parts.size == 1) {
                val id = parts[0]
                val method = exchange.requestMethod
                if (method == "DELETE") {
                    val deleted = todoStore.delete(id)
                    if (deleted) {
                        exchange.sendResponseHeaders(204, 0)
                        exchange.close()
                    } else {
                        exchange.sendResponseHeaders(404, 0)
                        exchange.close()
                    }
                } else {
                    exchange.sendResponseHeaders(405, 0)
                    exchange.close()
                }
                return@createContext
            }
            if (parts.size != 2 || parts[1] != "complete") {
                exchange.sendResponseHeaders(404, 0)
                exchange.close()
                return@createContext
            }
            val id = parts[0]
            val method = exchange.requestMethod
            if (method != "POST") {
                exchange.sendResponseHeaders(405, 0)
                exchange.close()
                return@createContext
            }
            val todo = todoStore.complete(id)
            if (todo == null) {
                exchange.sendResponseHeaders(404, 0)
                exchange.close()
            } else {
                val json = todoToJson(todo)
                exchange.responseHeaders.set("Content-Type", "application/json")
                exchange.sendResponseHeaders(200, json.toByteArray(StandardCharsets.UTF_8).size.toLong())
                val os = exchange.responseBody
                os.write(json.toByteArray(StandardCharsets.UTF_8))
                os.close()
            }
        } catch (e: Exception) {
            exchange.sendResponseHeaders(500, 0)
            exchange.close()
        }
    }
    
    server.createContext("/stats") { exchange ->
        try {
            val method = exchange.requestMethod
            if (method == "GET") {
                val stats = todoStore.stats()
                val json = """{"total":${stats.total},"completed":${stats.completed},"active":${stats.active}}"""
                exchange.responseHeaders.set("Content-Type", "application/json")
                exchange.sendResponseHeaders(200, json.toByteArray(StandardCharsets.UTF_8).size.toLong())
                val os = exchange.responseBody
                os.write(json.toByteArray(StandardCharsets.UTF_8))
                os.close()
            } else {
                exchange.sendResponseHeaders(405, 0)
                exchange.close()
            }
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
    println("Todos endpoint: http://localhost:$port/todos")
}

fun todoToJson(todo: Todo): String = 
    """{"id":"${escapeJson(todo.id)}","title":"${escapeJson(todo.title)}","completed":${todo.completed},"createdAt":${todo.createdAt}}"""

fun parseTitle(json: String): String? {
    val titleKeyRegex = "\"title\"\\s*:".toRegex()
    val matches = titleKeyRegex.findAll(json).toList()
    if (matches.isEmpty()) return null
    if (matches.size > 1) return "" // Signal duplicate key error
    val match = matches[0]
    val start = match.range.last + 1
    val trimmed = json.substring(start).trim()
    if (!trimmed.startsWith("\"")) return null
    val sb = StringBuilder()
    var i = 1
    var foundClosingQuote = false
    while (i < trimmed.length) {
        val c = trimmed[i]
        if (c == '\\' && i + 1 < trimmed.length) {
            val next = trimmed[i + 1]
            when (next) {
                '"' -> sb.append('"')
                '\\' -> sb.append('\\')
                'n' -> sb.append('\n')
                'r' -> sb.append('\r')
                't' -> sb.append('\t')
                else -> { sb.append(next) }
            }
            i += 2
        } else if (c == '"') {
            foundClosingQuote = true
            break
        } else {
            sb.append(c)
            i++
        }
    }
    if (!foundClosingQuote) return null
    return sb.toString()
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