package dev.danvega.todos.todo;

import java.time.LocalDate;

/** {@code source} is "web" for the browser, or the name of the MCP client that added it. */
public record Todo(long id, String title, String notes, Priority priority, LocalDate dueDate, boolean done, String source) {
}
