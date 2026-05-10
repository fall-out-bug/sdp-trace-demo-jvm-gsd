package com.example

import java.time.Instant
import kotlin.jvm.Synchronized

data class Todo(
    val id: String,
    val title: String,
    val completed: Boolean = false,
    val createdAt: Long = Instant.now().toEpochMilli()
)

class TodoStore {
    private val todos = mutableMapOf<String, Todo>()
    private var idCounter = 0

    @Synchronized
    fun create(title: String): Todo {
        idCounter++
        val todo = Todo(
            id = "todo-$idCounter",
            title = title,
            completed = false,
            createdAt = Instant.now().toEpochMilli()
        )
        todos[todo.id] = todo
        return todo
    }

    @Synchronized
    fun list(): List<Todo> = todos.values.toList()

    @Synchronized
    fun complete(id: String): Todo? {
        val existing = todos[id] ?: return null
        val updated = existing.copy(completed = true)
        todos[id] = updated
        return updated
    }

    @Synchronized
    fun update(id: String, title: String?, completed: Boolean?): Todo? {
        val existing = todos[id] ?: return null
        val updated = existing.copy(
            title = title ?: existing.title,
            completed = completed ?: existing.completed
        )
        todos[id] = updated
        return updated
    }
}